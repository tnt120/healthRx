package com.healthrx.backend.api.external.settings;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PasswordChangeRequest {
    private String currentPassword;
    private String newPassword;
    private String newPasswordConfirmation;
}
