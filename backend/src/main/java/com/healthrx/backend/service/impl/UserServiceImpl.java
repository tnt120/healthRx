package com.healthrx.backend.service.impl;

import com.healthrx.backend.api.external.UserVerificationRequest;
import com.healthrx.backend.api.internal.*;
import com.healthrx.backend.api.internal.enums.Role;
import com.healthrx.backend.repository.*;
import com.healthrx.backend.security.service.JwtService;
import com.healthrx.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.healthrx.backend.handler.BusinessErrorCodes.INVALID_VERIFICATION;
import static com.healthrx.backend.security.util.TokenType.VERIFICATION;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final SpecializationRepository specializationRepository;
    private final ParameterRepository parameterRepository;
    private final UserParameterRepository userParameterRepository;
    private final DoctorSpecializationRepository doctorSpecializationRepository;

    @Override
    public User verifyUser(UserVerificationRequest request) {
        User user = userRepository.findUserByEmail(jwtService.extractEmail(request.getVerificationToken(), VERIFICATION))
                .orElseThrow(INVALID_VERIFICATION::getError);

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setSex(request.getSex());
        user.setBirthDate(request.getBirthDate());
        user.setPhoneNumber(request.getPhoneNumber());

        if (user.getRole() == Role.USER) {
            user.setHeight(request.getHeight());
            AccountSettings accountSettings = new AccountSettings()
                    .setUser(user)
                    .setParametersNotifications(request.getParametersNotifications())
                    .setBadResultsNotificationsEnabled(request.isBadResultsNotificationsEnabled())
                    .setDrugNotificationsEnabled(request.isDrugNotificationsEnabled());

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
            DoctorDetails doctorDetails = new DoctorDetails()
                    .setUser(user)
                    .setNumberPWZ(request.getNumberPWZ())
                    .setNumberPESEL(request.getNumberPESEL())
                    .setIdPhotoFrontUrl(request.getIdPhotoFrontUrl())
                    .setIdPhotoBackUrl(request.getIdPhotoBackUrl());

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

        user.setConfigured(true);

        return userRepository.save(user);
    }
}
