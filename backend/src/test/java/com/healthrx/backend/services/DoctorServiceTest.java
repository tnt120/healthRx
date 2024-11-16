package com.healthrx.backend.services;

import com.healthrx.backend.api.external.DoctorResponse;
import com.healthrx.backend.api.external.PageResponse;
import com.healthrx.backend.api.external.ReVerifyDoctorRequest;
import com.healthrx.backend.api.external.SpecializationDTO;
import com.healthrx.backend.api.internal.*;
import com.healthrx.backend.api.internal.enums.Role;
import com.healthrx.backend.mapper.DoctorMapper;
import com.healthrx.backend.repository.DoctorDetailsRepository;
import com.healthrx.backend.repository.UserRepository;
import com.healthrx.backend.service.impl.DoctorServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.function.Supplier;

@ExtendWith(MockitoExtension.class)
@Slf4j
class DoctorServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private DoctorDetailsRepository doctorDetailsRepository;

    @Mock
    private DoctorMapper doctorMapper;

    @Mock
    private Supplier<User> principalSupplier;

    @InjectMocks
    private DoctorServiceImpl doctorService;

    @Test
    void shouldGetDoctorsWithPaginationAndFilters() {
        User user = new User().setId("1").setRole(Role.USER);
        Mockito.when(principalSupplier.get()).thenReturn(user);

        Specialization specialization = new Specialization().setId("1").setName("Cardiology");
        DoctorSpecialization doctorSpecialization = new DoctorSpecialization().setSpecialization(specialization);
        DoctorDetails doctorDetails = new DoctorDetails()
                .setDoctorSpecializations(List.of(doctorSpecialization))
                .setIsVerifiedDoctor(true);
        User doctor = new User()
                .setId("2")
                .setFirstName("John")
                .setLastName("Doe")
                .setRole(Role.DOCTOR)
                .setDoctorDetails(doctorDetails);

        DoctorResponse expectedResponse = DoctorResponse.builder()
                .id("2")
                .firstName("John")
                .lastName("Doe")
                .specializations(List.of(SpecializationDTO.builder()
                        .id("1")
                        .name("Cardiology")
                        .build()))
                .build();

        Page<User> doctorsPage = new PageImpl<>(List.of(doctor));

        Mockito.when(userRepository.findAll(Mockito.any(Specification.class), Mockito.any(Pageable.class)))
                .thenReturn(doctorsPage);

        Mockito.when(doctorMapper.map(Mockito.eq(doctor), Mockito.anyList()))
                .thenReturn(expectedResponse);

        PageResponse<DoctorResponse> result = doctorService.getDoctors(
                0, 10, "lastName", "asc", "John", "Doe", "1", "1");

        log.info("Result: {}", result);
        Assertions.assertNotNull(result, "Result should not be null");
        Assertions.assertNotNull(result.getContent(), "Content should not be null");
        Assertions.assertEquals(1, result.getTotalElements(), "Total elements should match");
        Assertions.assertFalse(result.getContent().isEmpty(), "Content should not be empty");
        Assertions.assertEquals(1, result.getContent().size(), "Content size should be 1");
        Assertions.assertEquals(expectedResponse, result.getContent().getFirst(), "Doctor response should match");
    }

    @Test
    void shouldReVerifyDoctorSuccessfully() {
        User doctor = new User()
                .setId("1")
                .setRole(Role.DOCTOR)
                .setDoctorDetails(new DoctorDetails().setNumberPESEL("123456789").setNumberPWZ("987654321"));

        ReVerifyDoctorRequest request = ReVerifyDoctorRequest.builder()
                .numberPESEL("111222333")
                .numberPWZ("444555666")
                .build();

        Mockito.when(principalSupplier.get()).thenReturn(doctor);

        doctorService.reVerifyDoctor(request);

        Assertions.assertEquals("111222333", doctor.getDoctorDetails().getNumberPESEL());
        Assertions.assertEquals("444555666", doctor.getDoctorDetails().getNumberPWZ());
        Mockito.verify(doctorDetailsRepository).save(Mockito.eq(doctor.getDoctorDetails()));
    }

    @Test
    void shouldThrowErrorWhenReVerifyCalledByNonDoctor() {
        User nonDoctor = new User()
                .setId("1")
                .setRole(Role.USER)
                .setDoctorDetails(new DoctorDetails());

        ReVerifyDoctorRequest request = ReVerifyDoctorRequest.builder().build();

        Mockito.when(principalSupplier.get()).thenReturn(nonDoctor);

        Assertions.assertThrows(RuntimeException.class, () -> doctorService.reVerifyDoctor(request));
    }

    @Test
    void shouldGetDoctorsWithoutFilters() {
        User user = new User().setId("1").setRole(Role.USER);
        Mockito.when(principalSupplier.get()).thenReturn(user);

        DoctorDetails doctorDetails = new DoctorDetails()
                .setIsVerifiedDoctor(true)
                .setDoctorSpecializations(List.of(
                        new DoctorSpecialization()
                                .setSpecialization(new Specialization().setId("2").setName("Dermatology"))
                ));
        User doctor = new User()
                .setId("3")
                .setFirstName("Jane")
                .setLastName("Doe")
                .setDoctorDetails(doctorDetails);

        DoctorResponse expectedDoctorResponse = DoctorResponse.builder()
                .id("3")
                .firstName("Jane")
                .lastName("Doe")
                .specializations(List.of())
                .build();

        Page<User> doctorsPage = new PageImpl<>(List.of(doctor));
        Mockito.when(userRepository.findAll(Mockito.any(Specification.class), Mockito.any(Pageable.class)))
                .thenReturn(doctorsPage);
        Mockito.when(doctorMapper.map(Mockito.eq(doctor), Mockito.anyList()))
                .thenReturn(expectedDoctorResponse);

        PageResponse<DoctorResponse> result = doctorService.getDoctors(0, 10, "lastName", "desc", null, null, null, null);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getTotalElements());
        Assertions.assertFalse(result.getContent().isEmpty());
        Assertions.assertEquals(expectedDoctorResponse, result.getContent().getFirst());
    }

}
