package com.healthrx.backend.service;

import com.healthrx.backend.api.external.settings.PasswordChangeRequest;
import com.healthrx.backend.api.external.settings.PersonalDataChangeRequest;

public interface SettingsService {
    Void personalDataChange(PersonalDataChangeRequest request);
    Void passwordChange(PasswordChangeRequest request);
}
