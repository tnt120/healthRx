package com.healthrx.backend.service;

import com.healthrx.backend.api.external.settings.PasswordChangeRequest;

public interface SettingsService {
    Void passwordChange(PasswordChangeRequest request);
}
