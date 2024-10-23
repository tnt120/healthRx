package com.healthrx.backend.service;

import com.healthrx.backend.api.external.settings.NotificationsData;
import com.healthrx.backend.api.external.settings.PasswordChangeRequest;
import com.healthrx.backend.api.external.settings.PersonalDataChangeRequest;
import com.healthrx.backend.api.internal.AccountSettings;
import com.healthrx.backend.api.internal.User;

public interface SettingsService {
    Void personalDataChange(PersonalDataChangeRequest request);
    Void passwordChange(PasswordChangeRequest request);
    NotificationsData notificationsChange(NotificationsData request);
    void processNotificationChange(NotificationsData request, AccountSettings accountSettings, User user);
}
