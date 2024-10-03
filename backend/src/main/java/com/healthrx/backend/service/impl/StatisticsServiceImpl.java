package com.healthrx.backend.service.impl;

import com.healthrx.backend.api.external.statistics.ChartDataDTO;
import com.healthrx.backend.api.external.statistics.ChartRequest;
import com.healthrx.backend.api.external.statistics.ChartResponse;
import com.healthrx.backend.api.internal.Parameter;
import com.healthrx.backend.api.internal.ParameterLog;
import com.healthrx.backend.api.internal.User;
import com.healthrx.backend.api.internal.enums.ChartType;
import com.healthrx.backend.repository.*;
import com.healthrx.backend.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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

    @Override
    public ChartResponse getChartData(ChartRequest request, ChartType type) {
        User user = principalSupplier.get();

        return switch (type) {
            case PARAMETER -> getParameterChartData(user, request);
            case DRUG -> getDrugChartData(user, request);
            case ACTIVITY -> getActivityChartData(user, request);
        };
    }

    private ChartResponse getParameterChartData(User user, ChartRequest request) {
        Parameter parameter = parameterRepository.findById(request.getDataId())
                .orElseThrow(PARAMETER_NOT_FOUND::getError);

        List<ParameterLog> logs = parameterLogRepository.findParameterLogsByParameterIdAndDateRange(
                request.getDataId(),
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
