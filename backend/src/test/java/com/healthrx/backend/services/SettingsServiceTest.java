package com.healthrx.backend.services;

import com.healthrx.backend.api.external.settings.NotificationsData;
import com.healthrx.backend.api.external.settings.PasswordChangeRequest;
import com.healthrx.backend.api.external.settings.PersonalDataChangeRequest;
import com.healthrx.backend.api.internal.*;
import com.healthrx.backend.api.internal.enums.Days;
import com.healthrx.backend.api.internal.enums.Priority;
import com.healthrx.backend.handler.BusinessErrorCodes;
import com.healthrx.backend.handler.ExceptionResponse;
import com.healthrx.backend.quartz.NotificationSchedulerService;
import com.healthrx.backend.quartz.QuartzNotificationDrugsModel;
import com.healthrx.backend.repository.AccountSettingsRepository;
import com.healthrx.backend.repository.UserDrugRepository;
import com.healthrx.backend.repository.UserRepository;
import com.healthrx.backend.service.impl.SettingsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.quartz.SchedulerException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SettingsServiceTest {

    @Mock
    private Supplier<User> principalSupplier;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserRepository userRepository;
    @Mock
    private AccountSettingsRepository accountSettingsRepository;
    @Mock
    private UserDrugRepository userDrugRepository;
    @Mock
    private NotificationSchedulerService notificationSchedulerService;

    @InjectMocks
    private SettingsServiceImpl settingsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldChangePersonalDataSuccessfully() {
        User user = User.builder().id("1").email("oldemail@example.com").build();
        PersonalDataChangeRequest request = PersonalDataChangeRequest.builder()
                .firstName("John")
                .lastName("Doe")
                .email("newemail@example.com")
                .build();

        when(principalSupplier.get()).thenReturn(user);
        when(userRepository.existsByEmail("newemail@example.com")).thenReturn(false);
        when(userRepository.save(user)).thenReturn(user);

        settingsService.personalDataChange(request);

        verify(userRepository).save(user);
        assertEquals("John", user.getFirstName());
        assertEquals("Doe", user.getLastName());
        assertEquals("newemail@example.com", user.getEmail());
    }

    @Test
    void shouldThrowErrorIfEmailAlreadyExists() {
        User user = User.builder().id("1").email("oldemail@example.com").build();
        PersonalDataChangeRequest request = PersonalDataChangeRequest.builder()
                .email("existingemail@example.com")
                .build();

        when(principalSupplier.get()).thenReturn(user);
        when(userRepository.existsByEmail("existingemail@example.com")).thenReturn(true);

        ExceptionResponse exception = assertThrows(ExceptionResponse.class, () -> settingsService.personalDataChange(request));
        assertEquals(BusinessErrorCodes.EMAIL_ALREADY_EXISTS.getError().getMessage(), exception.getMessage());
    }

    @Test
    void shouldChangePasswordSuccessfully() {
        User user = User.builder().id("1").password("hashedpassword").build();
        PasswordChangeRequest request = PasswordChangeRequest.builder()
                .currentPassword("oldpassword")
                .newPassword("newpassword")
                .newPasswordConfirmation("newpassword")
                .build();

        when(principalSupplier.get()).thenReturn(user);
        when(passwordEncoder.matches("oldpassword", "hashedpassword")).thenReturn(true);
        when(passwordEncoder.encode("newpassword")).thenReturn("newhashedpassword");

        settingsService.passwordChange(request);

        verify(userRepository).save(user);
        assertEquals("newhashedpassword", user.getPassword());
    }

    @Test
    void shouldThrowErrorForIncorrectCurrentPassword() {
        User user = User.builder().id("1").password("hashedpassword").build();
        PasswordChangeRequest request = PasswordChangeRequest.builder()
                .currentPassword("wrongpassword")
                .newPassword("newpassword")
                .newPasswordConfirmation("newpassword")
                .build();

        when(principalSupplier.get()).thenReturn(user);
        when(passwordEncoder.matches("wrongpassword", "hashedpassword")).thenReturn(false);

        ExceptionResponse exception = assertThrows(ExceptionResponse.class, () -> settingsService.passwordChange(request));
        assertEquals(BusinessErrorCodes.INCORRECT_CURRENT_PASSWORD.getError().getMessage(), exception.getMessage());
    }

    @Test
    void shouldChangeNotificationsSuccessfully() {
        User user = User.builder().id("1").email("user@example.com").build();
        AccountSettings accountSettings = AccountSettings.builder()
                .id("settings1")
                .isDrugNotificationsEnabled(false)
                .isBadResultsNotificationsEnabled(false)
                .parametersNotifications(LocalTime.of(10, 0))
                .build();
        NotificationsData request = NotificationsData.builder()
                .parametersNotifications(LocalTime.of(12, 0))
                .isBadResultsNotificationsEnabled(true)
                .isDrugNotificationsEnabled(true)
                .build();

        when(principalSupplier.get()).thenReturn(user);
        when(accountSettingsRepository.findAccountSettingsByUserId("1")).thenReturn(Optional.of(accountSettings));

        settingsService.notificationsChange(request);

        assertTrue(accountSettings.isDrugNotificationsEnabled());
        assertTrue(accountSettings.isBadResultsNotificationsEnabled());
        assertEquals(LocalTime.of(12, 0), accountSettings.getParametersNotifications());
    }

    @Test
    void shouldThrowErrorForInvalidNotificationsRequest() {
        User user = User.builder().id("1").build();
        NotificationsData request = NotificationsData.builder()
                .isDrugNotificationsEnabled(true)
                .build();

        when(principalSupplier.get()).thenReturn(user);
        when(accountSettingsRepository.findAccountSettingsByUserId("1")).thenReturn(Optional.of(new AccountSettings()));

        ExceptionResponse exception = assertThrows(ExceptionResponse.class, () -> settingsService.notificationsChange(request));
        assertEquals(BusinessErrorCodes.NOTIFICATIONS_DATA_BAD_REQUEST.getError().getMessage(), exception.getMessage());
    }

    @Test
    void shouldEnableDrugNotifications() throws SchedulerException {
        User user = User.builder().id("1").email("user@example.com").build();
        AccountSettings accountSettings = AccountSettings.builder()
                .id("settings1")
                .isDrugNotificationsEnabled(false)
                .isBadResultsNotificationsEnabled(false)
                .build();
        UserDrug userDrug = UserDrug.builder()
                .id("drug1")
                .drug(Drug.builder().id(1).name("Paracetamol").build())
                .priority(Priority.HIGH)
                .drugDoseDays(List.of(DrugDoseDay.builder().day(Days.MONDAY).build()))
                .drugDoseTimes(List.of(DrugDoseTime.builder().doseTime(LocalTime.of(10, 0)).build()))
                .build();

        NotificationsData request = NotificationsData.builder()
                .isDrugNotificationsEnabled(true)
                .isBadResultsNotificationsEnabled(false)
                .build();

        when(userDrugRepository.findAllByUserId("1")).thenReturn(List.of(userDrug));

        settingsService.processNotificationChange(request, accountSettings, user);

        verify(notificationSchedulerService).scheduleDrugNotification(
                eq("drugReminder"),
                eq("user@example.com"),
                any(QuartzNotificationDrugsModel.class)
        );
    }
}
