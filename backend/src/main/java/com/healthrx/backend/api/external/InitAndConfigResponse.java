package com.healthrx.backend.api.external;

import com.healthrx.backend.api.external.activities.ActivitiesResponse;
import com.healthrx.backend.api.external.settings.NotificationsData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InitAndConfigResponse {
    private UserResponse user;
    private List<ParameterDTO> parameters;
    private List<SpecializationDTO> specializations;
    private ActivitiesResponse activities;
    private List<CityDTO> cities;
    private List<UserParametersResponse> userParameters;
    private NotificationsData notificationsSettings;
}
