package com.healthrx.backend.service.impl;

import com.healthrx.backend.api.external.settings.NotificationsData;
import com.healthrx.backend.api.external.settings.PasswordChangeRequest;
import com.healthrx.backend.api.external.settings.PersonalDataChangeRequest;
import com.healthrx.backend.api.internal.*;
import com.healthrx.backend.api.internal.enums.Days;
import com.healthrx.backend.api.internal.enums.Priority;
import com.healthrx.backend.quartz.NotificationSchedulerService;
import com.healthrx.backend.quartz.QuartzNotificationDrugsModel;
import com.healthrx.backend.quartz.QuartzNotificationParametersModel;
import com.healthrx.backend.repository.AccountSettingsRepository;
import com.healthrx.backend.repository.UserDrugRepository;
import com.healthrx.backend.repository.UserRepository;
import com.healthrx.backend.service.SettingsService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.SchedulerException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

import static com.healthrx.backend.handler.BusinessErrorCodes.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class SettingsServiceImpl implements SettingsService {
    private final Supplier<User> principalSupplier;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final AccountSettingsRepository accountSettingsRepository;
    private final UserDrugRepository userDrugRepository;
    private final NotificationSchedulerService notificationSchedulerService;

    @Override
    @Transactional
    public Void personalDataChange(PersonalDataChangeRequest request) {
        User user = principalSupplier.get();

        Optional.ofNullable(request.getFirstName()).ifPresent(user::setFirstName);

        Optional.ofNullable(request.getLastName()).ifPresent(user::setLastName);

        if (request.getEmail() != null) {
            if (request.getEmail().equals(user.getEmail()) || userRepository.existsByEmail(request.getEmail())) {
                throw EMAIL_ALREADY_EXISTS.getError();
            }

            user.setEmail(request.getEmail());
        }

        Optional.ofNullable(request.getSex()).ifPresent(user::setSex);

        Optional.ofNullable(request.getBirthDate()).ifPresent(user::setBirthDate);

        Optional.ofNullable(request.getPhoneNumber()).ifPresent(user::setPhoneNumber);

        if (request.getIsHeightChanged() != null && request.getIsHeightChanged()) {
            user.setHeight(request.getHeight());
        }

        userRepository.save(user);

        return null;
    }

    @Override
    public Void passwordChange(PasswordChangeRequest request) {
        User user = principalSupplier.get();

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw INCORRECT_CURRENT_PASSWORD.getError();
        }

        if (!request.getNewPassword().equals(request.getNewPasswordConfirmation())) {
            throw NEW_PASSWORDS_NOT_MATCH.getError();
        }

        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw NO_DIFFERENT_NEW_PASSWORD.getError();
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));

        userRepository.save(user);

        log.info("Password changed for user: {}", user.getEmail());

        return null;
    }

    @Override
    @Transactional
    public NotificationsData notificationsChange(NotificationsData request) {
        User user = principalSupplier.get();

        AccountSettings accountSettings = accountSettingsRepository.findAccountSettingsByUserId(user.getId())
                .orElseThrow(ACCOUNT_SETTINGS_NOT_FOUND::getError);

        if (request.getIsBadResultsNotificationsEnabled() == null && request.getIsDrugNotificationsEnabled()) {
            throw NOTIFICATIONS_DATA_BAD_REQUEST.getError();
        }

        processNotificationChange(request, accountSettings, user);

        return null;
    }

    @Override
    public void processNotificationChange(NotificationsData request, AccountSettings accountSettings, User user) {
        if (request.getIsDrugNotificationsEnabled() != accountSettings.isDrugNotificationsEnabled()) {
            List<UserDrug> userDrugsPriorityHigh = userDrugRepository.findAllByUserId(user.getId())
                    .stream()
                    .filter(userDrug -> userDrug.getPriority().equals(Priority.HIGH))
                    .toList();

            userDrugsPriorityHigh.forEach(userDrug -> {
                List<Days> userDrugDays = userDrug.getDrugDoseDays().stream().map(DrugDoseDay::getDay).toList();
                List<LocalTime> userDrugTimes = userDrug.getDrugDoseTimes().stream().map(DrugDoseTime::getDoseTime).toList();

                try {
                    if (request.getIsDrugNotificationsEnabled()) {
                        QuartzNotificationDrugsModel quartzNotificationDrugsModel = QuartzNotificationDrugsModel.builder()
                                .userDrugId(userDrug.getId())
                                .drugName(userDrug.getDrug().getName())
                                .days(userDrugDays)
                                .times(userDrugTimes)
                                .startDate(userDrug.getStartDate())
                                .endDate(userDrug.getEndDate())
                                .build();

                        notificationSchedulerService.scheduleDrugNotification(
                                "drugReminder",
                                user.getEmail(),
                                quartzNotificationDrugsModel
                        );
                    } else {
                        notificationSchedulerService.deleteDrugNotification(
                                "drugReminder",
                                userDrug.getId(),
                                userDrugDays,
                                userDrugTimes
                        );
                    }
                } catch (SchedulerException e) {
                    log.info("Problem with editing drug notification after settings change: {}", e.getMessage());
                }
            });
        }

        if (!Objects.equals(request.getParametersNotifications(), accountSettings.getParametersNotifications())) {
            try {
                notificationSchedulerService.deleteParameterNotification(
                        "parameterReminder",
                        user.getId(), accountSettings.getParametersNotifications()
                );


                if (request.getParametersNotifications() != null) {
                    QuartzNotificationParametersModel quartzNotificationParametersModel = QuartzNotificationParametersModel.builder()
                            .userId(user.getId())
                            .time(request.getParametersNotifications())
                            .build();

                    notificationSchedulerService.scheduleParameterNotification(
                            "parameterReminder",
                            user.getEmail(),
                            quartzNotificationParametersModel
                    );
                }

            } catch (SchedulerException e) {
                log.info("Problem with editing parameter notification after settings change: {}", e.getMessage());
            }

        }

        accountSettings.setParametersNotifications(request.getParametersNotifications());
        accountSettings.setDrugNotificationsEnabled(request.getIsDrugNotificationsEnabled());
        accountSettings.setBadResultsNotificationsEnabled(request.getIsBadResultsNotificationsEnabled());
    }
}
