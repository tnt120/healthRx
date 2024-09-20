package com.healthrx.backend.api.external.invitation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvitationRequest {
    private String invitationId;
    private String targetDoctorId;
}
