package com.healthrx.backend.service.impl;

import com.healthrx.backend.api.external.*;
import com.healthrx.backend.api.external.admin.ChangeRoleReqRes;
import com.healthrx.backend.api.external.admin.DeleteUserRequest;
import com.healthrx.backend.api.external.settings.NotificationsData;
import com.healthrx.backend.api.internal.*;
import com.healthrx.backend.api.internal.enums.Role;
import com.healthrx.backend.handler.ExpiredTokenException;
import com.healthrx.backend.kafka.KafkaReceiveModel;
import com.healthrx.backend.mapper.*;
import com.healthrx.backend.quartz.NotificationSchedulerService;
import com.healthrx.backend.quartz.QuartzNotificationParametersModel;
import com.healthrx.backend.repository.*;
import com.healthrx.backend.security.aes.AesHandler;
import com.healthrx.backend.security.service.JwtService;
import com.healthrx.backend.service.ActivityService;
import com.healthrx.backend.service.AdminService;
import com.healthrx.backend.service.SettingsService;
import com.healthrx.backend.service.UserService;
import com.healthrx.backend.specification.UserSpecification;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.SchedulerException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static com.healthrx.backend.handler.BusinessErrorCodes.*;
import static com.healthrx.backend.security.util.TokenType.VERIFICATION;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final JwtService jwtService;
    private final AdminService adminService;
    private final SettingsService settingsService;
    private final UserRepository userRepository;
    private final SpecializationRepository specializationRepository;
    private final ParameterRepository parameterRepository;
    private final UserParameterRepository userParameterRepository;
    private final DoctorSpecializationRepository doctorSpecializationRepository;
    private final CityRepository cityRepository;
    private final ParameterLogRepository parameterLogRepository;
    private final ActivityLogRepository activityLogRepository;
    private final DrugLogRepository drugLogRepository;
    private final DoctorDetailsRepository doctorDetailsRepository;
    private final AccountSettingsRepository accountSettingsRepository;
    private final ParameterMapper parameterMapper;
    private final SpecializationMapper specializationMapper;
    private final UserParameterMapper userParameterMapper;
    private final CityMapper cityMapper;
    private final UserMapper userMapper;
    private final NotificationsMapper notificationsMapper;
    private final KafkaTemplate<String, KafkaReceiveModel> kafkaTemplate;
    private final Supplier<User> principalSupplier;
    private final ActivityService activityService;
    private final NotificationSchedulerService notificationSchedulerService;
    private final ImageService imageService;
    private final UserDrugRepository userDrugRepository;

    @Override
    @Transactional
    public PageResponse<UserResponse> getUsers(Integer page, Integer size, Role role, String firstName, String lastName) {
        adminService.checkPermissions();

        Sort sort = Sort.by("id").ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Specification<User> spec = Specification.where(null);
        if (firstName != null) spec = spec.and(UserSpecification.firstNameContains(firstName));
        if (lastName != null) spec = spec.and(UserSpecification.lastNameContains(lastName));
        if (role != null) spec = spec.and(UserSpecification.roleEquals(role));

        Page<User> users = userRepository.findAll(spec, pageable);

        List<UserResponse> userResponse = users.stream().map(user -> {
            Image profile = user.getProfilePicture();
            if (profile == null) {
                return userMapper.map(user);
            } else {
                return userMapper.mapWithProfile(user, AesHandler.decrypt(profile.getContent()));
            }
        }).toList();

        return new PageResponse<UserResponse>()
                .setContent(userResponse)
                .setCurrentPage(users.getNumber())
                .setPageSize(users.getSize())
                .setTotalElements(users.getTotalElements())
                .setLast(users.isLast())
                .setFirst(users.isFirst());
    }

    @Override
    public ChangeRoleReqRes changeRole(String userId, ChangeRoleReqRes req) {
        User admin = adminService.checkPermissions();

        User user = userRepository.findById(userId)
                .orElseThrow(USER_NOT_FOUND::getError);

        Role role = req.getRole();

        if (user.getRole().equals(Role.DOCTOR) || user.getRole().equals(Role.HEAD_ADMIN)) {
            throw CANNOT_CHANGE_ROLE.getError();
        }

        if (role.equals(Role.DOCTOR) || role.equals(Role.HEAD_ADMIN)) {
            throw CANNOT_SET_ROLE.getError();
        }

        if (user.getRole() == role) {
            throw USER_ALREADY_HAS_ROLE.getError();
        }

        if (user.getRole() == Role.ADMIN) {
            adminService.checkHeadAdminPermissions(admin);
        } else {
            disableNotifications(user);

        }

        user.setRole(role);
        userRepository.save(user);
        return ChangeRoleReqRes.builder().role(role).build();
    }

    @Override
    @Transactional
    public Void deleteUser(String userId, DeleteUserRequest req) {
        User admin = adminService.checkPermissions();

        User user = userRepository.findById(userId)
                .orElseThrow(USER_NOT_FOUND::getError);

        if (user.getRole() == Role.HEAD_ADMIN) {
            throw CANNOT_DELETE_HEAD_ADMIN.getError();
        }

        switch (user.getRole()) {
            case ADMIN:
                adminService.checkHeadAdminPermissions(admin);
                break;
            case DOCTOR:
                imageService.deletePwzPhotos(user.getDoctorDetails());
                doctorSpecializationRepository.deleteAllByDoctorDetailsId(user.getDoctorDetails().getId());
                break;
        }

        imageService.deleteProfilePictureByUser(user);
        disableNotifications(user);
        parameterLogRepository.deleteAllByUserId(userId);
        activityLogRepository.deleteAllByUserId(userId);
        drugLogRepository.deleteAllByUserId(userId);
        doctorDetailsRepository.deleteByUserId(userId);
        accountSettingsRepository.deleteByUserId(userId);
        userParameterRepository.deleteAllByUserId(userId);
        userDrugRepository.deleteAllByUserId(userId);
        userRepository.delete(user);

        Map<String, String> data = Map.of(
                "message", req.getMessage()
        );
        this.sendMail(Collections.singletonList(user.getEmail()), data, "ACCOUNT_DELETED", "Account deletion");

        return null;
    }

    @Override
    public User verifyUser(UserVerificationRequest request) {
        User user = userRepository.findUserByEmail(request.getEmail())
                .orElseThrow(INVALID_VERIFICATION::getError);

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setSex(request.getSex());
        user.setBirthDate(request.getBirthDate());
        user.setPhoneNumber(request.getPhoneNumber());

        fillUserDetails(user, request);

        user.setConfigured(true);

        if (user.getRole().equals(Role.USER) && request.getParametersNotifications() != null) {
            try {
                QuartzNotificationParametersModel quartzModel = QuartzNotificationParametersModel.builder()
                        .userId(user.getId())
                        .time(request.getParametersNotifications())
                        .build();

                notificationSchedulerService.scheduleParameterNotification(
                        "parameterReminder",
                        user.getEmail(),
                        quartzModel
                );
            } catch (SchedulerException e) {
                log.info("Problem with scheduling parameter notification: {}", e.getMessage());
            }

        }

        return userRepository.save(user);
    }

    @Override
    public VerificationDataResponse getVerificationData(Token request) {

        String verificationToken = request.getVerificationToken();

        log.info("Verification token: {}", verificationToken);

        if (verificationToken == null || verificationToken.isEmpty()) {
            throw INVALID_VERIFICATION.getError();
        }

        User user;

        try {
            user = userRepository.findUserByEmail(jwtService.extractEmail(verificationToken, VERIFICATION))
                    .orElseThrow(INVALID_VERIFICATION::getError);
        } catch (ExpiredTokenException e) {
            user = userRepository.findUserByEmail(e.getClaims().getSubject())
                    .orElseThrow(INVALID_VERIFICATION::getError);

            var newToken = jwtService.generateToken(user, VERIFICATION);
            Map<String, String> data = Map.of(
                    "link", "http://localhost:4200/verification/" + newToken
            );
            this.sendMail(Collections.singletonList(user.getEmail()), data, "VERIFICATION", "Account verification");
            throw VERIFICATION_TOKEN_EXPIRED.getError();
        }

        return VerificationDataResponse.builder()
                .userEmail(user.getEmail())
                .role(user.getRole().name())
                .parameters(
                        parameterRepository.findAll()
                                .stream()
                                .map(parameterMapper::map)
                                .toList()
                )
                .specializations(
                        specializationRepository.findAll()
                                .stream()
                                .map(specializationMapper::map)
                                .toList()
                )
                .cities(
                        cityRepository.findAll()
                                .stream()
                                .map(cityMapper::map)
                                .toList()
                )


                .build();
    }

    @Override
    public InitAndConfigResponse getInitAndConfigData() {

        User user = principalSupplier.get();

         InitAndConfigResponse response = new InitAndConfigResponse();

         response.setUser(userMapper.extendedMap(user));

         if (user.getRole() == Role.USER) {
             response.setActivities(activityService.getAllActivities());

             response.setParameters(
                     parameterRepository.findAll()
                             .stream()
                             .map(parameterMapper::map)
                             .toList()
             );

             response.setCities(
                     cityRepository.findAll()
                             .stream()
                             .map(cityMapper::map)
                             .toList()
             );

             response.setSpecializations(
                     specializationRepository.findAll()
                             .stream()
                             .map(specializationMapper::map)
                             .toList()
             );

             response.setUserParameters(
                     userParameterRepository.findAllByUserId(user.getId())
                             .stream()
                             .map(userParameter -> {
                                 Double parameterValue = parameterLogRepository.findParameterLogValueByParameterIdAndUserIdAndToday(
                                         userParameter.getParameter().getId(),
                                         userParameter.getUser().getId()
                                 ).orElse(null);

                                 return userParameterMapper.map(userParameter, parameterValue);
                             })
                             .toList()
             );

             response.setNotificationsSettings(
                     accountSettingsRepository.findAccountSettingsByUserId(user.getId())
                     .map(notificationsMapper::map)
                     .orElseThrow(ACCOUNT_SETTINGS_NOT_FOUND::getError)
             );
         } else if (user.getRole() == Role.DOCTOR) {
             UserResponse us = response.getUser();
             us.setIsDoctorVerified(user.getIsVerifiedDoctor());
             if (!user.getIsVerifiedDoctor()) {
                 us.setUnverifiedDoctor(UnverifiedDoctorDTO.builder()
                                 .unverifiedMessage(user.getDoctorDetails().getUnverifiedMessage())
                                 .numberPESEL(user.getDoctorDetails().getNumberPESEL())
                                .numberPWZ(user.getDoctorDetails().getNumberPWZ())
                         .build());
             };
         }

        return response;
    }

    @Override
    public User getUser(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(USER_NOT_FOUND::getError);
    }

    private void sendMail(List<String> emails, Map<String, String> data, String strategy, String subject) {
        KafkaReceiveModel kafkaReceiveModel = new KafkaReceiveModel()
                .setSubject(subject)
                .setStrategy(strategy)
                .setEmails(emails)
                .setData(data);

        this.kafkaTemplate.send("mails", kafkaReceiveModel);
    }

    private void fillUserDetails(User user, UserVerificationRequest request) {
        if (user.getRole() == Role.USER) {
            user.setHeight(request.getHeight());
            AccountSettings accountSettings = AccountSettings.builder()
                    .user(user)
                    .parametersNotifications(request.getParametersNotifications())
                    .isBadResultsNotificationsEnabled(request.getIsBadResultsNotificationsEnabled())
                    .isDrugNotificationsEnabled(request.getIsDrugNotificationsEnabled())
                    .build();

            user.setAccountSettings(accountSettings);

            request.getParameters().forEach(parameter -> {
                UserParameter userParameter = new UserParameter()
                        .setUser(user)
                        .setParameter(
                                parameterRepository.getReferenceById(parameter.getId())
                        );

                userParameterRepository.save(userParameter);
            });
        } else if (user.getRole() == Role.DOCTOR) {
            City city = this.cityRepository.findById(request.getCity().getId()).orElse(null);

            DoctorDetails doctorDetails = new DoctorDetails()
                    .setUser(user)
                    .setNumberPWZ(request.getNumberPWZ())
                    .setNumberPESEL(request.getNumberPESEL())
                    .setCity(city);

            user.setDoctorDetails(doctorDetails);

            request.getSpecializations().forEach(specialization -> {
                DoctorSpecialization doctorSpecialization = new DoctorSpecialization()
                        .setDoctorDetails(doctorDetails)
                        .setSpecialization(
                                specializationRepository.getReferenceById(specialization.getId())
                        );

                doctorSpecializationRepository.save(doctorSpecialization);
            });
        }
    }

    private void disableNotifications(User user) {
        AccountSettings accountSettings = user.getAccountSettings();
        if (accountSettings != null) {
            settingsService.processNotificationChange(
                    NotificationsData.builder()
                    .isBadResultsNotificationsEnabled(false)
                    .isDrugNotificationsEnabled(false)
                    .parametersNotifications(null).build(),
                    accountSettings,
                    user
            );
        }
    }
}
