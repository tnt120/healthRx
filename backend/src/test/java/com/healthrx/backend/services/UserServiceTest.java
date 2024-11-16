package com.healthrx.backend.services;

import com.healthrx.backend.api.external.*;
import com.healthrx.backend.api.external.activities.ActivitiesResponse;
import com.healthrx.backend.api.external.settings.NotificationsData;
import com.healthrx.backend.api.internal.*;
import com.healthrx.backend.api.internal.enums.Role;
import com.healthrx.backend.repository.*;
import com.healthrx.backend.mapper.*;
import com.healthrx.backend.service.impl.ActivityServiceImpl;
import com.healthrx.backend.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {
    @Mock
    private ParameterRepository parameterRepository;
    @Mock
    private UserParameterRepository userParameterRepository;
    @Mock
    private SpecializationRepository specializationRepository;
    @Mock
    private CityRepository cityRepository;
    @Mock
    private AccountSettingsRepository accountSettingsRepository;
    @Mock
    private ParameterLogRepository parameterLogRepository;
    @Mock
    private ParameterMapper parameterMapper;
    @Mock
    private SpecializationMapper specializationMapper;
    @Mock
    private CityMapper cityMapper;
    @Mock
    private UserMapper userMapper;
    @Mock
    private UserParameterMapper userParameterMapper;
    @Mock
    private NotificationsMapper notificationsMapper;
    @Mock
    private ActivityServiceImpl activityService;
    @Mock
    private Supplier<User> principalSupplier;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldReturnInitAndConfigDataForUser() {
        User user = User.builder().id("1").role(Role.USER).build();
        Parameter parameter = Parameter.builder().id("p1").name("Height").build();
        UserParameter userParameter = UserParameter.builder().id("up1").parameter(parameter).user(user).build();
        AccountSettings accountSettings = AccountSettings.builder().id("as1").isDrugNotificationsEnabled(true).build();

        when(principalSupplier.get()).thenReturn(user);
        when(parameterRepository.findAll()).thenReturn(List.of(parameter));
        when(userParameterRepository.findAllByUserId("1")).thenReturn(List.of(userParameter));
        when(accountSettingsRepository.findAccountSettingsByUserId("1")).thenReturn(Optional.of(accountSettings));
        when(parameterMapper.map(parameter)).thenReturn(ParameterDTO.builder().id("p1").name("Height").build());
        when(userParameterMapper.map(eq(userParameter), any())).thenReturn(UserParametersResponse.builder().id("up1").build());
        when(notificationsMapper.map(accountSettings)).thenReturn(NotificationsData.builder().isDrugNotificationsEnabled(true).build());
        when(activityService.getAllActivities()).thenReturn(new ActivitiesResponse());
        when(parameterLogRepository.findParameterLogValueByParameterIdAndUserIdAndToday(any(), any()))
                .thenReturn(Optional.of(10.0));

        InitAndConfigResponse response = userService.getInitAndConfigData();

        assertNotNull(response);
        assertNotNull(response.getUserParameters());
        assertEquals(1, response.getUserParameters().size());
        assertEquals("p1", response.getParameters().get(0).getId());
        assertTrue(response.getNotificationsSettings().getIsDrugNotificationsEnabled());
    }

    @Test
    void shouldReturnInitAndConfigDataForDoctor() {
        User user = User.builder()
                .id("1")
                .role(Role.DOCTOR)
                .doctorDetails(DoctorDetails.builder()
                        .isVerifiedDoctor(false)
                        .unverifiedMessage("Verification pending")
                        .numberPESEL("1234567890")
                        .numberPWZ("PWZ123456")
                        .build())
                .build();
        Specialization specialization = Specialization.builder().id("s1").name("Cardiology").build();
        City city = City.builder().id("c1").name("New York").build();

        when(principalSupplier.get()).thenReturn(user);
        when(specializationRepository.findAll()).thenReturn(List.of(specialization));
        when(cityRepository.findAll()).thenReturn(List.of(city));
        when(specializationMapper.map(any(Specialization.class)))
                .thenReturn(SpecializationDTO.builder().id("s1").name("Cardiology").build());
        when(cityMapper.map(any(City.class)))
                .thenReturn(CityDTO.builder().id("c1").name("New York").build());
        when(userMapper.extendedMap(any(User.class)))
                .thenReturn(UserResponse.builder()
                        .id("1")
                        .unverifiedDoctor(UnverifiedDoctorDTO.builder()
                                .unverifiedMessage("Verification pending")
                                .numberPESEL("1234567890")
                                .numberPWZ("PWZ123456")
                                .build())
                        .build());

        InitAndConfigResponse response = userService.getInitAndConfigData();

        assertNotNull(response, "Response should not be null");
        assertNull(response.getSpecializations());
        assertNull(response.getCities(), "Cities should not be null");
        assertNotNull(response.getUser(), "User response should not be null");
        assertEquals("Verification pending", response.getUser().getUnverifiedDoctor().getUnverifiedMessage(),
                "Unverified message should match");
    }

    @Test
    void shouldHandleInitConfigAndDataForDoctorWithoutVerificationDetails() {
        User user = User.builder().id("1").role(Role.DOCTOR).doctorDetails(
                DoctorDetails.builder()
                        .isVerifiedDoctor(true)
                        .build()
        ).build();

        when(principalSupplier.get()).thenReturn(user);
        when(userMapper.extendedMap(user)).thenReturn(UserResponse.builder().id("1").build());

        InitAndConfigResponse response = userService.getInitAndConfigData();

        assertNotNull(response);
        assertNotNull(response.getUser());
        assertNotNull(user.getDoctorDetails());
        assertTrue(user.getDoctorDetails().getIsVerifiedDoctor());
    }
}
