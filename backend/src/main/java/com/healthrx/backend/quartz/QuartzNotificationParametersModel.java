package com.healthrx.backend.quartz;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalTime;

@Data
@Accessors(chain = true)
@Builder
public class QuartzNotificationParametersModel {
    String userId;
    LocalTime time;
}
