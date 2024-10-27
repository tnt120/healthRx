package com.healthrx.backend.api.external.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardDataResponse {
    private Integer users;
    private Integer doctors;
    private Integer pendingApprovals;
    private Integer activities;
    private Integer parameters;
    private Integer drugs;
}
