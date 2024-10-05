package com.healthrx.backend.service.impl;

import com.healthrx.backend.api.external.statistics.*;
import com.healthrx.backend.api.internal.Parameter;
import com.healthrx.backend.api.internal.ParameterLog;
import com.healthrx.backend.api.internal.User;
import com.healthrx.backend.api.internal.UserParameter;
import com.healthrx.backend.api.internal.enums.ChartType;
import com.healthrx.backend.api.internal.enums.TrendType;
import com.healthrx.backend.mapper.ParameterMapper;
import com.healthrx.backend.repository.*;
import com.healthrx.backend.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static com.healthrx.backend.handler.BusinessErrorCodes.PARAMETER_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatisticsServiceImpl implements StatisticsService {
    private final Supplier<User> principalSupplier;
    private final ParameterRepository parameterRepository;
    private final DrugRepository drugRepository;
    private final ActivityRepository activityRepository;
    private final ParameterLogRepository parameterLogRepository;
    private final DrugLogRepository drugLogRepository;
    private final UserParameterRepository userParameterRepository;
    private final ParameterMapper parameterMapper;

    @Override
    public ChartResponse getChartData(ChartRequest request, ChartType type) {
        User user = principalSupplier.get();

        return switch (type) {
            case PARAMETER -> getParameterChartData(user, request);
            case DRUG -> getDrugChartData(user, request);
            case ACTIVITY -> getActivityChartData(user, request);
        };
    }

    @Override
    public List<ParameterStatisticsResponse> getParametersStatistics(StatisticsRequest req) {
        User user = principalSupplier.get();

        List<Parameter> parameters = userParameterRepository.findAllByUserId(user.getId())
                .stream()
                .map(UserParameter::getParameter)
                .toList();

        List<ParameterStatisticsResponse> response = new ArrayList<>();

        parameters.forEach(parameter -> {
            List<ParameterLog> logs = parameterLogRepository.findParameterLogsByParameterIdAndUserIdAndDateRange(
                    parameter.getId(),
                    user.getId(),
                    req.getStartDate(),
                    req.getEndDate()
            );

            if (logs.isEmpty()) {
                response.add(ParameterStatisticsResponse.builder()
                                .parameter(parameterMapper.map(parameter))
                                .firstLogDate(null)
                                .lastLogDate(null)
                                .avgValue(0.0)
                                .minValue(0.0)
                                .maxValue(0.0)
                                .daysBelowMinValue(0)
                                .daysAboveMaxValue(0)
                                .logsCount(0)
                                .missedDays(0)
                                .longestBreak(0)
                                .trend(null)
                                .build());

                return;
            }

            SimpleRegression regression = new SimpleRegression();

            ParameterStatisticsResponse parametersRes = new ParameterStatisticsResponse();

            double sum = 0.0;
            double paramMinValue = Double.parseDouble(parameter.getMinValue());
            double paramMaxValue = Double.parseDouble(parameter.getMaxValue());
            int daysBelowMinValue = 0;
            int daysAboveMaxValue = 0;
            double min = Double.MAX_VALUE;
            double max = Double.MIN_VALUE;
            long longestBreak = 0;

            LocalDate startDate = logs.getFirst().getCreatedAt().toLocalDate();
            LocalDate prevDate = startDate;

            for (ParameterLog log : logs) {
                LocalDate currLogDate = log.getCreatedAt().toLocalDate();

                long dayIndex = ChronoUnit.DAYS.between(startDate, currLogDate);
                regression.addData(dayIndex, log.getValue());

                sum += log.getValue();
                min = Math.min(min, log.getValue());
                max = Math.max(max, log.getValue());

                if (log.getValue() < paramMinValue) {
                    daysBelowMinValue++;
                } else if (log.getValue() > paramMaxValue) {
                    daysAboveMaxValue++;
                }

                longestBreak = Math.max(longestBreak, ChronoUnit.DAYS.between(prevDate, currLogDate) - 1);

                prevDate = currLogDate;
            }

            parametersRes.setParameter(parameterMapper.map(parameter));

            double range = paramMaxValue - paramMinValue;
            double threshold = range * 0.05;
            double stabilityThreshold = range * 0.01;

            log.info("Parameter: {} Range: {}, Threshold: {}, Stability Threshold: {} slope: {}", parameter.getName(), range, threshold, stabilityThreshold, regression.getSlope());

            parametersRes.setTrend(TrendType.fromSlope(regression.getSlope(), threshold, stabilityThreshold));


            parametersRes.setFirstLogDate(logs.getFirst().getCreatedAt());
            parametersRes.setLastLogDate(logs.getLast().getCreatedAt());
            parametersRes.setAvgValue(sum / logs.size());
            parametersRes.setMinValue(min);
            parametersRes.setMaxValue(max);
            parametersRes.setDaysBelowMinValue(daysBelowMinValue);
            parametersRes.setDaysAboveMaxValue(daysAboveMaxValue);
            parametersRes.setLogsCount(logs.size());

            LocalDate firstLogDate = logs.getFirst().getCreatedAt().toLocalDate();
            LocalDate endDate = req.getEndDate().toLocalDate();

            long totalDays = ChronoUnit.DAYS.between(firstLogDate, endDate) + 1;
            int missedDays = Math.abs((int) (totalDays - logs.size()));
            parametersRes.setMissedDays(missedDays);

            longestBreak = Math.max(longestBreak, ChronoUnit.DAYS.between(prevDate, endDate));

            parametersRes.setLongestBreak((int) longestBreak);

            response.add(parametersRes);
        });

        return response;
    }

    private ChartResponse getParameterChartData(User user, ChartRequest request) {
        Parameter parameter = parameterRepository.findById(request.getDataId())
                .orElseThrow(PARAMETER_NOT_FOUND::getError);

        List<ParameterLog> logs = parameterLogRepository.findParameterLogsByParameterIdAndUserIdAndDateRange(
                request.getDataId(),
                user.getId(),
                request.getStartDate(),
                request.getEndDate()
        );

        return ChartResponse.builder()
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .name(parameter.getName())
                .data(logs.stream().map(log -> ChartDataDTO.builder()
                        .label(log.getCreatedAt().toString())
                        .value(log.getValue())
                        .build())
                        .toList())
                .build();
    }

    private ChartResponse getDrugChartData(User user, ChartRequest request) {
        log.info("Getting drug chart data for user {}", user.getId());
        return new ChartResponse();
    }

    private ChartResponse getActivityChartData(User user, ChartRequest request) {
        log.info("Getting activity chart data for user {}", user.getId());
        return new ChartResponse();
    }
}
