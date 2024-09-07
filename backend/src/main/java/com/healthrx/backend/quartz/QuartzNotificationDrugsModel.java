package com.healthrx.backend.quartz;

import com.healthrx.backend.api.internal.enums.Days;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Data
@Accessors(chain = true)
@Builder
public class QuartzNotificationDrugsModel {
    String userDrugId;
    String drugName;
    List<Days> days;
    List<LocalTime> times;
    LocalDateTime startDate;
    LocalDateTime endDate;
}
