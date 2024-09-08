package com.healthrx.backend.api.external;

import com.healthrx.backend.api.internal.enums.Days;
import com.healthrx.backend.api.internal.enums.Priority;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDrugsRequest {
    private Integer drugId;
    private Integer doseSize;
    private Priority priority;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer amount;
    private List<LocalTime> doseTimes;
    private List<Days> doseDays;
}
