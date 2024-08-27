package com.healthrx.backend.service.impl;

import com.healthrx.backend.api.external.InitAndConfigResponse;
import com.healthrx.backend.api.external.Token;
import com.healthrx.backend.api.external.UserVerificationRequest;
import com.healthrx.backend.api.external.VerificationDataResponse;
import com.healthrx.backend.api.internal.*;
import com.healthrx.backend.api.internal.enums.Role;
import com.healthrx.backend.handler.ExpiredTokenException;
import com.healthrx.backend.kafka.KafkaReceiveModel;
import com.healthrx.backend.mapper.*;
import com.healthrx.backend.repository.*;
import com.healthrx.backend.security.service.JwtService;
import com.healthrx.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final ActivityRepository activityRepository;
    private final ParameterLogRepository parameterLogRepository;
    private final ParameterMapper parameterMapper;
    private final SpecializationMapper specializationMapper;
    private final UserParameterMapper userParameterMapper;
    private final CityMapper cityMapper;
    private final UserMapper userMapper;
    private final ActivityMapper activityMapper;
    private final KafkaTemplate<String, KafkaReceiveModel> kafkaTemplate;
    private final Supplier<User> principalSupplier;

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

         response.setUser(userMapper.map(user));

         if (user.getRole() == Role.USER) {
             response.setActivities(
                     activityRepository.findAll()
                             .stream()
                             .map(activityMapper::map)
                             .toList()
             );

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
                    .setIdPhotoFrontUrl(request.getIdPhotoFrontUrl())
                    .setIdPhotoBackUrl(request.getIdPhotoBackUrl())
                    .setCity(city);

            user.setDoctorDetails(doctorDetails);
            user.setPictureUrl(request.getProfilePictureUrl());

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
