package com.healthrx.backend.api.external;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FriendshipPermissions {
    private Boolean userMedicineAccess;
    private Boolean activitiesAccess;
    private Boolean parametersAccess;
}
