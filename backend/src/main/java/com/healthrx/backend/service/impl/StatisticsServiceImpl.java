package com.healthrx.backend.service.impl;

import com.healthrx.backend.api.external.statistics.*;
import com.healthrx.backend.api.internal.*;
import com.healthrx.backend.api.internal.enums.ChartType;
import com.healthrx.backend.api.internal.enums.Days;
import com.healthrx.backend.api.internal.enums.TrendType;
import com.healthrx.backend.mapper.ParameterMapper;
import com.healthrx.backend.repository.*;
import com.healthrx.backend.service.StatisticsService;
import com.healthrx.backend.utils.ComplianceStats;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.healthrx.backend.handler.BusinessErrorCodes.DRUG_NOT_FOUND;
import static com.healthrx.backend.handler.BusinessErrorCodes.PARAMETER_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatisticsServiceImpl implements StatisticsService {
    private final Supplier<User> principalSupplier;
    private final ParameterRepository parameterRepository;
    private final DrugRepository drugRepository;
    private final UserDrugRepository userDrugRepository;
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
        Drug drug = drugRepository.findById(Integer.parseInt(request.getDataId()))
                .orElseThrow(DRUG_NOT_FOUND::getError);

        UserDrug userDrug = userDrugRepository.findUserDrugByUserIdAndDrugId(drug.getId(), user.getId())
                .orElseThrow(DRUG_NOT_FOUND::getError);

        List<DrugLog> logs = drugLogRepository.findDrugLogsByDrugIdAndUserIdAndDateRange(
                drug.getId(),
                user.getId(),
                request.getStartDate(),
                request.getEndDate()
        );

        Map<DayOfWeek, ComplianceStats> complianceStatsMap = initComplianceStats();
        Set<DayOfWeek> plannedDaysOfWeek = getPlannedDaysOfWeek(userDrug);
        int totalPlannedDoses = 0;

        for (DayOfWeek day : plannedDaysOfWeek) {
            int plannedDosesForDay = calculatePlannedDosesForDay(userDrug, request.getStartDate().toLocalDate(), request.getEndDate().toLocalDate(), day);
            complianceStatsMap.get(day).setPlanned(plannedDosesForDay);
            totalPlannedDoses += plannedDosesForDay;
        }

        logs.forEach(log -> processDrugLog(log, complianceStatsMap));

        List<ChartDataDTO> chartData = new ArrayList<>();

        for (DayOfWeek day : plannedDaysOfWeek) {
            ComplianceStats stats = complianceStatsMap.get(day);

            chartData.add(ChartDataDTO.builder()
                    .label(Days.from(day).toString())
                    .value(stats.calcTotalPercentTaken())
                    .additionalValue(stats.calcTotalPercentOnTime())
                    .build());
        }

        int totalTakenDoses = complianceStatsMap.values().stream().mapToInt(ComplianceStats::getTaken).sum();
        int totalOnTimeDoses = complianceStatsMap.values().stream().mapToInt(ComplianceStats::getOnTime).sum();

        chartData.add(ChartDataDTO.builder()
                .label("Total")
                .value(totalPlannedDoses > 0 ? (totalTakenDoses * 100.0 / totalPlannedDoses) : 0)
                .additionalValue(totalTakenDoses > 0 ? (totalOnTimeDoses * 100.0 / totalTakenDoses) : 0)
                .build());

        return ChartResponse.builder()
                .name("Drug Compliance and Punctuality by Day of the Week")
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .data(chartData)
                .build();
    }

    private Map<DayOfWeek, ComplianceStats> initComplianceStats() {
        Map<DayOfWeek, ComplianceStats> map = new EnumMap<>(DayOfWeek.class);
        for (DayOfWeek day : DayOfWeek.values()) {
            map.put(day, new ComplianceStats());
        }
        return map;
    }

    private Set<DayOfWeek> getPlannedDaysOfWeek(UserDrug userDrug) {
        return userDrug.getDrugDoseDays().stream()
                .map(DrugDoseDay::getDay)
                .map(Days::toDayOfWeek)
                .sorted(Comparator.comparingInt(DayOfWeek::getValue))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private int calculatePlannedDosesForDay(UserDrug userDrug, LocalDate startDate, LocalDate endDate, DayOfWeek day) {
        LocalDate start = startDate.isAfter(userDrug.getStartDate()) ? startDate : userDrug.getStartDate();
        LocalDate end;
        if (userDrug.getEndDate() == null) {
            end = endDate;
        } else {
            end = endDate.isBefore(userDrug.getEndDate()) ? endDate : userDrug.getEndDate();
        }

        if (start.isAfter(end)) {
            return 0;
        }

        LocalDate firstOccurrence = start.with(TemporalAdjusters.nextOrSame(day));
        if (firstOccurrence.isAfter(end)) {
            return 0;
        }

        int totalDays = (int) ChronoUnit.DAYS.between(firstOccurrence, end);
        int countOfSpecificDay = 1 + totalDays / 7;

        return countOfSpecificDay * userDrug.getDrugDoseTimes().size();
    }

    private void processDrugLog(DrugLog log, Map<DayOfWeek, ComplianceStats> complianceStatsMap) {
        DayOfWeek day = log.getDay().toDayOfWeek();
        ComplianceStats stats = complianceStatsMap.get(day);
        stats.incrementTaken();

        int minutesDifference = (int) ChronoUnit.MINUTES.between(log.getTime(), log.getTakenTime());

        if (Math.abs(minutesDifference) <= 15) {
            stats.incrementOnTime();
        }
    }

    private ChartResponse getActivityChartData(User user, ChartRequest request) {
        log.info("Getting activity chart data for user {}", user.getId());
        return new ChartResponse();
    }
}
