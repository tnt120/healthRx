package com.healthrx.backend.api.external;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.healthrx.backend.api.internal.enums.Priority;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDrugMonitorResponse {
    private String id;
    private DrugResponse drug;
    private Double doseSize;
    private Priority priority;
    private LocalTime time;
    @JsonInclude()
    private LocalTime takenTime;
}
