package com.healthrx.backend.service.impl;

import com.healthrx.backend.api.external.DoctorResponse;
import com.healthrx.backend.api.external.PageResponse;
import com.healthrx.backend.api.internal.DoctorSpecialization;
import com.healthrx.backend.api.internal.User;
import com.healthrx.backend.mapper.DoctorMapper;
import com.healthrx.backend.repository.DoctorDetailsRepository;
import com.healthrx.backend.repository.DoctorSpecializationRepository;
import com.healthrx.backend.repository.UserRepository;
import com.healthrx.backend.service.DoctorService;
import com.healthrx.backend.specification.DoctorSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Supplier;

import static com.healthrx.backend.handler.BusinessErrorCodes.DOCTOR_DETAILS_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorService {
    private final UserRepository userRepository;
    private final DoctorSpecializationRepository doctorSpecializationRepository;
    private final DoctorDetailsRepository doctorDetailsRepository;
    private final DoctorMapper doctorMapper;
    private final Supplier<User> principalSupplier;

    @Override
    public PageResponse<DoctorResponse> getDoctors(Integer page, Integer size, String sortBy, String order, String firstName, String lastName, String specializationId, String cityId) {
        User user = principalSupplier.get(); // dodac potem sprawdzenie czy doktor nie jest już jego friendshipem

        Sort sort = order.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Specification<User> specification = Specification.where(DoctorSpecification.isVerifiedDoctor());
        if (firstName != null) specification = specification.and(DoctorSpecification.firstNameContains(firstName));
        if (lastName != null) specification = specification.and(DoctorSpecification.lastNameContains(lastName));
        if (specializationId != null) specification = specification.and(DoctorSpecification.specificationEquals(specializationId));
        if (cityId != null) specification = specification.and(DoctorSpecification.cityEquals(cityId));

        Page<User> doctors = userRepository.findAll(specification, pageable);

        List<DoctorResponse> doctorsResponse = doctors.getContent()
                .stream()
                .map(doctor -> doctorMapper
                        .map(
                                doctor,
                                doctorDetailsRepository.findByUserId(doctor.getId()).orElseThrow(DOCTOR_DETAILS_NOT_FOUND::getError),
                                doctorSpecializationRepository.findAllByDoctorDetailsId(doctor.getDoctorDetails().getId()).stream()
                                        .map(DoctorSpecialization::getSpecialization)
                                        .toList()))
                .toList();

        return new PageResponse<DoctorResponse>()
                .setContent(doctorsResponse)
                .setCurrentPage(doctors.getNumber())
                .setPageSize(doctors.getSize())
                .setTotalElements(doctors.getTotalElements())
                .setLast(doctors.isLast())
                .setFirst(doctors.isFirst());
    }
}
