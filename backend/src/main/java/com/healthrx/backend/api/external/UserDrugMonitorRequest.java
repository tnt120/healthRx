package com.healthrx.backend.api.external;

import com.healthrx.backend.api.internal.enums.Days;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDrugMonitorRequest {
    private String id;
    private Integer drugId;
    private Days day;
    private LocalTime time;
    private LocalTime takenTime;
}
