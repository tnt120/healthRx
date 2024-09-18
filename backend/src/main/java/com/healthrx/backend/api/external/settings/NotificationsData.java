package com.healthrx.backend.api.external.settings;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationsData {
    @JsonInclude()
    private LocalTime parametersNotifications;
    private Boolean isBadResultsNotificationsEnabled;
    private Boolean isDrugNotificationsEnabled;
}
