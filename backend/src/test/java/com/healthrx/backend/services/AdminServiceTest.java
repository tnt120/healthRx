package com.healthrx.backend.services;

import com.healthrx.backend.api.external.DoctorResponse;
import com.healthrx.backend.api.external.PageResponse;
import com.healthrx.backend.api.external.SpecializationDTO;
import com.healthrx.backend.api.external.admin.DashboardDataResponse;
import com.healthrx.backend.api.external.admin.DoctorVerificationRequest;
import com.healthrx.backend.api.external.image.ImageResponse;
import com.healthrx.backend.api.internal.DoctorDetails;
import com.healthrx.backend.api.internal.DoctorSpecialization;
import com.healthrx.backend.api.internal.Specialization;
import com.healthrx.backend.api.internal.User;
import com.healthrx.backend.api.internal.enums.ImageType;
import com.healthrx.backend.api.internal.enums.Role;
import com.healthrx.backend.mapper.DoctorMapper;
import com.healthrx.backend.repository.*;
import com.healthrx.backend.service.AdminService;
import com.healthrx.backend.service.impl.AdminServiceImpl;
import com.healthrx.backend.service.impl.ImageService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ImageService imageService;

    @Mock
    private DoctorMapper doctorMapper;

    @Mock
    private Supplier<User> principalSupplier;

    @Mock
    private DrugRepository drugRepository;

    @Mock
    private ActivityRepository activityRepository;

    @Mock
    private ParameterRepository parameterRepository;

    @Mock
    private DoctorDetailsRepository doctorDetailsRepository;

    @InjectMocks
    private AdminServiceImpl adminService;

    @Test
    void shouldReturnDashboardData() {
        // Arrange
        User admin = new User().setId("1").setRole(Role.ADMIN);
        Mockito.when(principalSupplier.get()).thenReturn(admin);

        Mockito.when(userRepository.countAllByRole(Role.USER)).thenReturn(100);
        Mockito.when(userRepository.countAllByRole(Role.DOCTOR)).thenReturn(50);
        Mockito.when(drugRepository.count()).thenReturn(20L);
        Mockito.when(activityRepository.count()).thenReturn(30L);
        Mockito.when(parameterRepository.count()).thenReturn(10L);
        Mockito.when(doctorDetailsRepository.countAllByIsVerifiedDoctorIsFalseAndUnverifiedMessageIsNull()).thenReturn(5);

        // Act
        DashboardDataResponse response = adminService.getDashboardData();

        // Assert
        Assertions.assertNotNull(response);
        Assertions.assertEquals(100, response.getUsers());
        Assertions.assertEquals(50, response.getDoctors());
        Assertions.assertEquals(20, response.getDrugs());
        Assertions.assertEquals(30, response.getActivities());
        Assertions.assertEquals(10, response.getParameters());
        Assertions.assertEquals(5, response.getPendingApprovals());
    }

    @Test
    void shouldVerifyDoctorSuccessfully() {
        User admin = new User().setId("1").setRole(Role.ADMIN);
        Mockito.when(principalSupplier.get()).thenReturn(admin);

        User doctor = new User().setId("2").setRole(Role.DOCTOR);
        DoctorDetails doctorDetails = new DoctorDetails().setIsVerifiedDoctor(false);
        doctor.setDoctorDetails(doctorDetails);

        DoctorVerificationRequest request = DoctorVerificationRequest.builder()
                .doctorId("2")
                .validVerification(true)
                .build();

        Mockito.when(userRepository.findById("2")).thenReturn(Optional.of(doctor));

        adminService.verifyDoctor(request);

        Assertions.assertTrue(doctor.getDoctorDetails().getIsVerifiedDoctor());
        Mockito.verify(imageService).deletePwzPhotos(doctorDetails);
    }

    @Test
    void shouldDenyDoctorVerification() {
        User admin = new User().setId("1").setRole(Role.ADMIN);
        Mockito.when(principalSupplier.get()).thenReturn(admin);

        User doctor = new User().setId("2").setRole(Role.DOCTOR);
        DoctorDetails doctorDetails = new DoctorDetails().setIsVerifiedDoctor(false);
        doctor.setDoctorDetails(doctorDetails);

        DoctorVerificationRequest request = DoctorVerificationRequest.builder()
                .doctorId("2")
                .validVerification(false)
                .message("Documents are invalid")
                .build();

        Mockito.when(userRepository.findById("2")).thenReturn(Optional.of(doctor));

        adminService.verifyDoctor(request);

        Assertions.assertEquals("Documents are invalid", doctor.getDoctorDetails().getUnverifiedMessage());
        Mockito.verify(imageService).deletePwzPhotos(doctorDetails);
    }

    @Test
    void shouldReturnApprovalsPage() {
        User admin = new User().setId("1").setRole(Role.ADMIN);
        Mockito.when(principalSupplier.get()).thenReturn(admin);

        DoctorDetails doctorDetails = new DoctorDetails()
                .setDoctorSpecializations(List.of(
                        new DoctorSpecialization().setSpecialization(new Specialization().setId("1").setName("Cardiology"))
                ));
        User doctor = new User()
                .setId("2")
                .setFirstName("John")
                .setLastName("Doe")
                .setDoctorDetails(doctorDetails);

        ImageResponse frontPwzImage = ImageResponse.builder()
                .imageType(ImageType.FRONT_PWZ_PHOTO)
                .image(new byte[]{1, 2, 3})
                .build();

        ImageResponse backPwzImage = ImageResponse.builder()
                .imageType(ImageType.BACK_PWZ_PHOTO)
                .image(new byte[]{4, 5, 6})
                .build();

        DoctorResponse expectedResponse = DoctorResponse.builder()
                .id("2")
                .firstName("John")
                .lastName("Doe")
                .specializations(List.of(SpecializationDTO.builder()
                        .id("1")
                        .name("Cardiology")
                        .build()))
                .frontPwz(frontPwzImage.getImage())
                .backPwz(backPwzImage.getImage())
                .build();

        Page<User> doctorsPage = new PageImpl<>(List.of(doctor), PageRequest.of(0, 10), 1);
        Mockito.when(userRepository.findAll(Mockito.any(Specification.class), Mockito.any(PageRequest.class)))
                .thenReturn(doctorsPage);

        Mockito.when(imageService.getPictures(Mockito.any(), Mockito.any()))
                .thenReturn(List.of(frontPwzImage, backPwzImage));

        Mockito.when(doctorMapper.extendedVerifyMap(
                Mockito.eq(doctor),
                Mockito.anyList(),
                Mockito.eq(frontPwzImage.getImage()),
                Mockito.eq(backPwzImage.getImage())
        )).thenReturn(expectedResponse);

        PageResponse<DoctorResponse> result = adminService.getApprovals(0, 10);

        Assertions.assertNotNull(result, "Result should not be null");
        Assertions.assertEquals(1, result.getTotalElements(), "Total elements should match");
        Assertions.assertFalse(result.getContent().isEmpty(), "Content should not be empty");
        Assertions.assertEquals(expectedResponse, result.getContent().get(0), "Doctor response should match");
        Mockito.verify(imageService).getPictures(Mockito.any(), Mockito.any());
    }


    @Test
    void shouldThrowErrorForNonAdminPermissions() {
        User user = new User().setId("1").setRole(Role.USER);
        Mockito.when(principalSupplier.get()).thenReturn(user);

        Assertions.assertThrows(RuntimeException.class, adminService::checkPermissions);
    }

    @Test
    void shouldAllowAdminOrHeadAdminPermissions() {
        User admin = new User().setId("1").setRole(Role.ADMIN);
        Mockito.when(principalSupplier.get()).thenReturn(admin);

        User result = adminService.checkPermissions();

        Assertions.assertNotNull(result);
        Assertions.assertEquals(admin, result);
    }
}
