package com.healthrx.backend.service.impl;

import com.healthrx.backend.api.external.DoctorResponse;
import com.healthrx.backend.api.external.PageResponse;
import com.healthrx.backend.api.external.admin.DoctorVerificationRequest;
import com.healthrx.backend.api.external.image.ImageRequest;
import com.healthrx.backend.api.external.image.ImageResponse;
import com.healthrx.backend.api.internal.DoctorDetails;
import com.healthrx.backend.api.internal.DoctorSpecialization;
import com.healthrx.backend.api.internal.User;
import com.healthrx.backend.api.internal.enums.ImageType;
import com.healthrx.backend.api.internal.enums.Role;
import com.healthrx.backend.mapper.DoctorMapper;
import com.healthrx.backend.repository.UserRepository;
import com.healthrx.backend.service.AdminService;
import com.healthrx.backend.specification.DoctorSpecification;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static com.healthrx.backend.handler.BusinessErrorCodes.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final UserRepository userRepository;
    private final ImageService imageService;
    private final DoctorMapper doctorMapper;
    private final Supplier<User> principalSupplier;

    @Override
    public Void verifyDoctor(DoctorVerificationRequest req) {
        checkPermissions();

        User doctor = userRepository.findById(req.getDoctorId())
                .orElseThrow(USER_NOT_FOUND::getError);

        if (doctor.getRole() != Role.DOCTOR) throw USER_NOT_FOUND.getError();
        if (doctor.getIsVerifiedDoctor()) throw DOCTOR_ALREADY_VERIFIED.getError();


        if (req.getValidVerification()) {
            doctor.setIsVerifiedDoctor(true);
        } else {
            DoctorDetails doctorDetails = doctor.getDoctorDetails();
            if (doctorDetails.getUnverifiedMessage() != null && !doctorDetails.getUnverifiedMessage().isEmpty()) throw DOCTOR_ALREADY_NOT_VERIFIED.getError();

            doctorDetails.setUnverifiedMessage(req.getMessage());
        }

        imageService.deletePwzPhotos(doctor.getDoctorDetails());

        return null;
    }

    @Override
    @Transactional
    public PageResponse<DoctorResponse> getApprovals(Integer page, Integer size) {
        checkPermissions();

        Sort sort = Sort.by("id").ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Specification<User> specification = Specification.where(DoctorSpecification.isDoctorForVerification());

        Page<User> doctors = userRepository.findAll(specification, pageable);

        List<DoctorResponse> doctorsResponse = doctors.getContent()
                .stream()
                .map(doctor -> {
                    List<ImageResponse> pwzImages = imageService.getPictures(new ImageRequest(List.of(ImageType.FRONT_PWZ_PHOTO, ImageType.BACK_PWZ_PHOTO)), Optional.of(doctor));

                    return doctorMapper.extendedVerifyMap(
                            doctor,
                            doctor.getDoctorDetails().getDoctorSpecializations().stream().map(DoctorSpecialization::getSpecialization).toList(),
                            pwzImages.get(0).getImage(),
                            pwzImages.get(1).getImage()
                            );
                }).toList();

        return new PageResponse<DoctorResponse>()
                .setContent(doctorsResponse)
                .setCurrentPage(doctors.getNumber())
                .setPageSize(doctors.getSize())
                .setTotalElements(doctors.getTotalElements())
                .setLast(doctors.isLast())
                .setFirst(doctors.isFirst());
    }

    @Override
    public User checkPermissions() {
        User user = principalSupplier.get();

        if (user.getRole() != Role.ADMIN && user.getRole() != Role.HEAD_ADMIN) {
            throw USER_NOT_PERMITTED.getError();
        }

        return user;
    }
}
