package com.healthrx.backend.service.impl;

import com.healthrx.backend.api.external.*;
import com.healthrx.backend.api.internal.*;
import com.healthrx.backend.api.internal.enums.Role;
import com.healthrx.backend.handler.ExpiredTokenException;
import com.healthrx.backend.kafka.KafkaReceiveModel;
import com.healthrx.backend.mapper.*;
import com.healthrx.backend.quartz.NotificationSchedulerService;
import com.healthrx.backend.quartz.QuartzNotificationParametersModel;
import com.healthrx.backend.repository.*;
import com.healthrx.backend.security.service.JwtService;
import com.healthrx.backend.service.ActivityService;
import com.healthrx.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.SchedulerException;
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
    private final UserRepository userRepository;
    private final SpecializationRepository specializationRepository;
    private final ParameterRepository parameterRepository;
    private final UserParameterRepository userParameterRepository;
    private final DoctorSpecializationRepository doctorSpecializationRepository;
    private final CityRepository cityRepository;
    private final ParameterLogRepository parameterLogRepository;
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
            this.sendMail(Collections.singletonList(user.getEmail()), newToken);
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
             us.setUnverifiedMessage(user.getDoctorDetails().getUnverifiedMessage());
         }

        return response;
    }

    private void sendMail(List<String> emails, String verificationToken) {
        KafkaReceiveModel kafkaReceiveModel = new KafkaReceiveModel()
                .setSubject("Account verification")
                .setStrategy("VERIFICATION")
                .setEmails(emails)
                .setData(Map.of(
                        "link", "http://localhost:4200/verification/" + verificationToken
                ));

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
}
