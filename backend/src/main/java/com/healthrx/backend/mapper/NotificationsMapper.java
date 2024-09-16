package com.healthrx.backend.mapper;

import com.healthrx.backend.api.external.settings.NotificationsData;
import com.healthrx.backend.api.internal.AccountSettings;
import org.springframework.stereotype.Component;

@Component
public class NotificationsMapper {

    public NotificationsData map(AccountSettings accountSettings) {
        return NotificationsData.builder()
                .isBadResultsNotificationsEnabled(accountSettings.isBadResultsNotificationsEnabled())
                .isDrugNotificationsEnabled(accountSettings.isDrugNotificationsEnabled())
                .parametersNotifications(accountSettings.getParametersNotifications())
                .build();
    }
}
