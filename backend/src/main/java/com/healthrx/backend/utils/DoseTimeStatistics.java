package com.healthrx.backend.utils;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DoseTimeStatistics {
    private Double avgDelay;
    private Double punctualityPercentage;
}
