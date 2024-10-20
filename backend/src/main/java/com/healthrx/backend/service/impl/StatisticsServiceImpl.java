package com.healthrx.backend.service.impl;

import com.healthrx.backend.api.external.statistics.*;
import com.healthrx.backend.api.internal.*;
import com.healthrx.backend.api.internal.enums.ChartType;
import com.healthrx.backend.api.internal.enums.Days;
import com.healthrx.backend.api.internal.enums.TrendType;
import com.healthrx.backend.mapper.ActivityMapper;
import com.healthrx.backend.mapper.DrugMapper;
import com.healthrx.backend.mapper.ParameterMapper;
import com.healthrx.backend.repository.*;
import com.healthrx.backend.service.StatisticsService;
import com.healthrx.backend.utils.models.ComplianceStats;
import com.healthrx.backend.utils.models.DoseTimeStatistics;
import com.healthrx.backend.utils.models.DrugDayStatistics;
import com.healthrx.backend.utils.statistics.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;
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
    private final ActivityLogRepository activityLogRepository;
    private final ParameterLogRepository parameterLogRepository;
    private final DrugLogRepository drugLogRepository;
    private final UserParameterRepository userParameterRepository;
    private final ParameterMapper parameterMapper;
    private final DrugMapper drugMapper;
    private final ActivityMapper activityMapper;
    private final TimeStatisticsCalculator timeStatisticsCalculator;
    private final DayStatisticsCalculator dayStatisticsCalculator;

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

        return generateParameterStats(user.getId(), req.getStartDate(), req.getEndDate());
    }

    @Override
    public List<ParameterStatisticsResponse> generateParameterStats(String userId, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        List<Parameter> parameters = userParameterRepository.findAllByUserId(userId)
                .stream()
                .map(UserParameter::getParameter)
                .toList();

        List<ParameterStatisticsResponse> res = new ArrayList<>();

        parameters.forEach(parameter -> {
            List<ParameterLog> logs = getParameterLogs(parameter.getId(), userId, startDateTime, endDateTime);

            if (logs.isEmpty()) {
                res.add(ParameterStatisticsResponse.builder()
                        .parameter(parameterMapper.map(parameter))
                        .build());
                return;
            }

            SimpleRegression regression = new SimpleRegression();

            ParameterStatisticsResponse parametersRes = new ParameterStatisticsResponse();

            double sum = 0.0;
            Double paramMinStandardValue = parameter.getMinStandardValue();
            Double paramMaxStandardValue = parameter.getMaxStandardValue();
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

                if (log.getValue() < paramMinStandardValue) {
                    daysBelowMinValue++;
                } else if (log.getValue() > paramMaxStandardValue) {
                    daysAboveMaxValue++;
                }

                longestBreak = Math.max(longestBreak, ChronoUnit.DAYS.between(prevDate, currLogDate) - 1);

                prevDate = currLogDate;
            }

            parametersRes.setParameter(parameterMapper.map(parameter));

            double range = paramMaxStandardValue - paramMinStandardValue;
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
            LocalDate endDate = endDateTime.toLocalDate();

            long totalDays = ChronoUnit.DAYS.between(firstLogDate, endDate) + 1;
            int missedDays = Math.abs((int) (totalDays - logs.size()));
            parametersRes.setMissedDays(missedDays);

            longestBreak = Math.max(longestBreak, ChronoUnit.DAYS.between(prevDate, endDate));

            parametersRes.setLongestBreak((int) longestBreak);

            res.add(parametersRes);
        });

        return res;
    }

    @Override
    public List<DrugStatisticsResponse> getDrugsStatistics(StatisticsRequest req) {
        User user = principalSupplier.get();

        return generateDrugStats(user.getId(), req.getStartDate(), req.getEndDate());
    }

    @Override
    public List<DrugStatisticsResponse> generateDrugStats(String userId, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        List<UserDrug> userDrugs = userDrugRepository.findAllByUserId(userId);

        List<DrugStatisticsResponse> res = new ArrayList<>();

        userDrugs.forEach(userDrug -> {
            Drug drug = userDrug.getDrug();

            List<DrugLog> logs = drugLogRepository.findDrugLogsByDrugIdAndUserIdAndDateRange(
                    drug.getId(),
                    userId,
                    startDateTime,
                    endDateTime
            );

            if (logs.isEmpty()) {
                res.add(DrugStatisticsResponse.builder()
                        .drug(drugMapper.simpleMap(drug))
                        .build());
                return;
            }

            DrugDayStatistics dayStatistics = dayStatisticsCalculator.calculateDayStatistics(userDrug, logs, startDateTime, endDateTime);

            int totalDosesTaken = logs.size();
            int totalDosesPlanned = dayStatisticsCalculator.calculateTotalPlannedDoses(userDrug, startDateTime.toLocalDate(), endDateTime.toLocalDate());
            int missedDoses = totalDosesPlanned - totalDosesTaken;
            double compliancePercentage = totalDosesPlanned > 0 ? (totalDosesTaken * 100.0 / totalDosesPlanned) : 0;

            DoseTimeStatistics timeStatistics = timeStatisticsCalculator.calculateTimeStatistics(logs);

            res.add(
                    DrugStatisticsResponse.builder()
                            .drug(drugMapper.simpleMap(drug))
                            .totalDosesTaken(totalDosesTaken)
                            .totalDosesMissed(missedDoses)
                            .totalDaysTaken(dayStatistics.getTotalDaysTaken())
                            .totalDaysMissed(dayStatistics.getTotalDaysMissed())
                            .totalDaysPartiallyTaken(dayStatistics.getTotalDaysPartiallyTaken())
                            .compliancePercentage(compliancePercentage)
                            .punctualityPercentage(timeStatistics.getPunctualityPercentage())
                            .avgDelay(timeStatistics.getAvgDelay())
                            .firstLogDate(logs.getFirst().getCreatedAt().toLocalDate())
                            .lastLogDate(logs.getLast().getCreatedAt().toLocalDate())
                            .build()
            );

        });

        return res;
    }

    @Override
    public List<ActivityStatisticsResponse> getActivitiesStatistics(StatisticsRequest req) {
        User user = principalSupplier.get();

        return generateActivityStats(user.getId(), req.getStartDate(), req.getEndDate());
    }

    @Override
    public List<ActivityStatisticsResponse> generateActivityStats(String userId, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        List<ActivityLog> logs = activityLogRepository.findActivityLogsByUserIdAndDateRange(
                userId,
                startDateTime,
                endDateTime
        );

        if (logs.isEmpty()) {
            return new ArrayList<>();
        }

        Map<Activity, List<ActivityLog>> logsGroupedByActivity = logs.stream()
                .collect(Collectors.groupingBy(ActivityLog::getActivity));

        List<ActivityStatisticsResponse> res = new ArrayList<>();

        for (Map.Entry<Activity, List<ActivityLog>> entry : logsGroupedByActivity.entrySet()) {
            Activity activity = entry.getKey();
            List<ActivityLog> activityLogs = entry.getValue();
            int logsCount = activityLogs.size();

            IntSummaryStatistics durationStats = activityLogs.stream()
                    .mapToInt(ActivityLog::getDuration)
                    .summaryStatistics();

            double hoursCount = durationStats.getSum() / 60.0;

            Optional<IntSummaryStatistics> heartRateStats = getStatistics(activityLogs, log -> Optional.ofNullable(log.getAverageHeartRate()).orElse(0));
            Integer minHeartRate = heartRateStats.map(IntSummaryStatistics::getMin).orElse(null);
            Integer maxHeartRate = heartRateStats.map(IntSummaryStatistics::getMax).orElse(null);
            Double avgHeartRate = heartRateStats.map(IntSummaryStatistics::getAverage).orElse(null);

            Optional<IntSummaryStatistics> caloriesBurnedStats = getStatistics(activityLogs, log -> Optional.ofNullable(log.getCaloriesBurned()).orElse(0));
            Integer minCaloriesBurned = caloriesBurnedStats.map(IntSummaryStatistics::getMin).orElse(null);
            Integer maxCaloriesBurned = caloriesBurnedStats.map(IntSummaryStatistics::getMax).orElse(null);
            Double avgCaloriesBurned = caloriesBurnedStats.map(IntSummaryStatistics::getAverage).orElse(null);

            res.add(ActivityStatisticsResponse.builder()
                    .activity(activityMapper.map(activity))
                    .minDuration(durationStats.getMin())
                    .maxDuration(durationStats.getMax())
                    .avgDuration(durationStats.getAverage())
                    .minHeartRate(minHeartRate)
                    .maxHeartRate(maxHeartRate)
                    .avgHeartRate(avgHeartRate)
                    .minCaloriesBurned(minCaloriesBurned)
                    .maxCaloriesBurned(maxCaloriesBurned)
                    .avgCaloriesBurned(avgCaloriesBurned)
                    .logsCount(logsCount)
                    .hoursCount(hoursCount)
                    .firstLogDate(activityLogs.getFirst().getActivityTime())
                    .lastLogDate(activityLogs.getLast().getActivityTime())
                    .build());
        }

        return res;
    }

    private Optional<IntSummaryStatistics> getStatistics(List<ActivityLog> logs, ToIntFunction<ActivityLog> mapper) {
        IntSummaryStatistics stats = logs.stream()
                .mapToInt(mapper)
                .filter(value -> value > 0)
                .summaryStatistics();

        return stats.getCount() > 0 ? Optional.of(stats) : Optional.empty();
    }

    private ChartResponse getParameterChartData(User user, ChartRequest request) {
        Parameter parameter = parameterRepository.findById(request.getDataId())
                .orElseThrow(PARAMETER_NOT_FOUND::getError);

        List<ParameterLog> logs = getParameterLogs(request.getDataId(), user.getId(), request.getStartDate(), request.getEndDate());

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

    private List<ParameterLog> getParameterLogs(String dataId, String userId, LocalDateTime startDate, LocalDateTime endDate) {
        return parameterLogRepository.findParameterLogsByParameterIdAndUserIdAndDateRange(
                dataId,
                userId,
                startDate,
                endDate
        );
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

        if (logs.isEmpty()) {
            return ChartResponse.builder()
                    .name("Drug Compliance and Punctuality by Day of the Week")
                    .startDate(request.getStartDate())
                    .endDate(request.getEndDate())
                    .data(new ArrayList<>())
                    .build();
        }

        Map<DayOfWeek, ComplianceStats> complianceStatsMap = initComplianceStats();
        Set<DayOfWeek> plannedDaysOfWeek = dayStatisticsCalculator.getPlannedDaysOfWeek(userDrug);
        int totalPlannedDoses = 0;

        for (DayOfWeek day : plannedDaysOfWeek) {
            int plannedDosesForDay = dayStatisticsCalculator.calculatePlannedDosesForDay(userDrug, request.getStartDate().toLocalDate(), request.getEndDate().toLocalDate(), day);
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

    private void processDrugLog(DrugLog log, Map<DayOfWeek, ComplianceStats> complianceStatsMap) {
        DayOfWeek day = log.getDay().toDayOfWeek();
        ComplianceStats stats = complianceStatsMap.get(day);
        stats.incrementTaken();

        int minutesDifference = (int) ChronoUnit.MINUTES.between(log.getTime(), log.getTakenTime());

        if (Math.abs(minutesDifference) <= 15) {
            stats.incrementOnTime();
        }
    }

    private ChartResponse getActivityChartData(User user, ChartRequest req) {
        List<ActivityLog> logs = activityLogRepository.findActivityLogsByUserIdAndDateRange(
                user.getId(),
                req.getStartDate(),
                req.getEndDate()
        );

        if (logs.isEmpty()) {
            return ChartResponse.builder()
                    .name("Activity Logs")
                    .startDate(req.getStartDate())
                    .endDate(req.getEndDate())
                    .data(new ArrayList<>())
                    .build();
        }

        Map<String, List<ActivityLog>> logsGroupedByActivity = logs.stream()
                .collect(Collectors.groupingBy(log -> log.getActivity().getName()));

        List<ChartDataDTO> chartData = logsGroupedByActivity.entrySet().stream()
                .map(entry -> {
                    String name = entry.getKey();
                    List<ActivityLog> activityLogs = entry.getValue();

                    double logCount = activityLogs.size();
                    double totalDuration = activityLogs.stream().mapToInt(ActivityLog::getDuration).sum();

                    return ChartDataDTO.builder()
                            .label(name)
                            .value(logCount)
                            .additionalValue(totalDuration)
                            .build();
                })
                .toList();

        return ChartResponse.builder()
                .name("Activity Logs")
                .startDate(req.getStartDate())
                .endDate(req.getEndDate())
                .data(chartData)
                .build();
    }
}
