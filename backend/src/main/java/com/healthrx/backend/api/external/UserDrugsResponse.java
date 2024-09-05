package com.healthrx.backend.api.external;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.healthrx.backend.api.internal.enums.Days;
import com.healthrx.backend.api.internal.enums.Priority;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDrugsResponse {
    private String id;
    private DrugResponse drug;
    private Double doseSize;
    private Priority priority;
    private LocalDateTime startDate;
    @JsonInclude()
    private LocalDateTime endDate;
    @JsonInclude()
    private Integer amount;
    private List<LocalTime> doseTimes;
    private List<Days> doseDays;
}
