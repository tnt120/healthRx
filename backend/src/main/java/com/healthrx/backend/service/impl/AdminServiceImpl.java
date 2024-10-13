package com.healthrx.backend.service.impl;

import com.healthrx.backend.api.external.admin.DoctorVerificationRequest;
import com.healthrx.backend.api.internal.User;
import com.healthrx.backend.api.internal.enums.Role;
import com.healthrx.backend.repository.UserRepository;
import com.healthrx.backend.service.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.function.Supplier;

import static com.healthrx.backend.handler.BusinessErrorCodes.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final UserRepository userRepository;
    private final ImageService imageService;
    private final Supplier<User> principalSupplier;

    @Override
    public Void verifyDoctor(DoctorVerificationRequest req) {
        User user = principalSupplier.get();
        checkPermissions(user);

        User doctor = userRepository.findById(req.getDoctorId())
                .orElseThrow(USER_NOT_FOUND::getError);

        if (doctor.getRole() != Role.DOCTOR)    throw USER_NOT_FOUND.getError();
        if (doctor.getIsVerifiedDoctor())       throw DOCTOR_ALREADY_VERIFIED.getError();

        doctor.setIsVerifiedDoctor(true);
        imageService.deletePwzPhotos(doctor.getDoctorDetails());

        return null;
    }

    private void checkPermissions(User user) {
        if (user.getRole() != Role.ADMIN && user.getRole() != Role.HEAD_ADMIN) {
            throw USER_NOT_PERMITTED.getError();
        }
    }
}
