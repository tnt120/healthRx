package com.healthrx.backend.api.external.admin;

import com.healthrx.backend.api.internal.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChangeRoleReqRes {
    private Role role;
}
