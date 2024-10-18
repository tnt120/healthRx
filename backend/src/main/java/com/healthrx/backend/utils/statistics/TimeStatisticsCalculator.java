package com.healthrx.backend.utils.statistics;

import com.healthrx.backend.api.internal.DrugLog;
import com.healthrx.backend.utils.models.DoseTimeStatistics;
import org.springframework.stereotype.Component;

import java.time.temporal.ChronoUnit;
import java.util.List;
@Component
public class TimeStatisticsCalculator {
    public DoseTimeStatistics calculateTimeStatistics(List<DrugLog> logs) {
        double totalDelay = 0;
        int onTimeDoses = 0;

        for (DrugLog log : logs) {
            int delay = Math.abs((int) ChronoUnit.MINUTES.between(log.getTime(), log.getTakenTime()));
            totalDelay += delay;

            if (delay <= 15) {
                onTimeDoses++;
            }
        }

        double avgDelay = totalDelay / logs.size();
        double punctualityPercentage = onTimeDoses * 100.0 / logs.size();

        return DoseTimeStatistics.builder().avgDelay(avgDelay).punctualityPercentage(punctualityPercentage).build();
    }
}
