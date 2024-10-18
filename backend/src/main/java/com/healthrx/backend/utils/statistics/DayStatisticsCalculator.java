package com.healthrx.backend.utils.statistics;

import com.healthrx.backend.api.internal.DrugDoseDay;
import com.healthrx.backend.api.internal.DrugLog;
import com.healthrx.backend.api.internal.UserDrug;
import com.healthrx.backend.api.internal.enums.Days;
import com.healthrx.backend.utils.models.DrugDayStatistics;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class DayStatisticsCalculator {
    public DrugDayStatistics calculateDayStatistics(UserDrug userDrug, List<DrugLog> logs, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        int dosesPerDay = userDrug.getDrugDoseTimes().size();

        Map<LocalDate, List<DrugLog>> logsByDate = logs.stream()
                .collect(Collectors.groupingBy(log -> log.getCreatedAt().toLocalDate()));

        Set<DayOfWeek> plannedDaysOfWeek = getPlannedDaysOfWeek(userDrug);

        LocalDate currDate = startDateTime.toLocalDate().isAfter(userDrug.getStartDate()) ? startDateTime.toLocalDate() : userDrug.getStartDate();
        LocalDate endDate = getEndDate(userDrug, endDateTime.toLocalDate());

        int totalDaysTaken = 0;
        int totalDaysPartiallyTaken = 0;
        int totalDaysMissed = 0;

        if (!plannedDaysOfWeek.contains(currDate.getDayOfWeek())) {
            currDate = findNextPlannedDate(currDate, plannedDaysOfWeek);
        }

        while (!currDate.isAfter(endDate)) {
            List<DrugLog> logsForDay = logsByDate.getOrDefault(currDate, Collections.emptyList());

            if (logsForDay.isEmpty()) {
                totalDaysMissed++;
            } else if (logsForDay.size() == dosesPerDay) {
                totalDaysTaken++;
            } else {
                totalDaysPartiallyTaken++;
            }

            currDate = findNextPlannedDate(currDate.plusDays(1), plannedDaysOfWeek);
        }

        return DrugDayStatistics.builder().totalDaysTaken(totalDaysTaken).totalDaysMissed(totalDaysMissed).totalDaysPartiallyTaken(totalDaysPartiallyTaken).build();
    }

    public int calculateTotalPlannedDoses(UserDrug userDrug, LocalDate startDate, LocalDate endDate) {
        Set<DayOfWeek> plannedDaysOfWeek = getPlannedDaysOfWeek(userDrug);

        int totalPlannedDoses = 0;

        for (DayOfWeek day : plannedDaysOfWeek) {
            totalPlannedDoses += calculatePlannedDosesForDay(userDrug, startDate, endDate, day);
        }

        return totalPlannedDoses;
    }

    public int calculatePlannedDosesForDay(UserDrug userDrug, LocalDate startDate, LocalDate endDate, DayOfWeek day) {
        LocalDate start = startDate.isAfter(userDrug.getStartDate()) ? startDate : userDrug.getStartDate();
        LocalDate end = getEndDate(userDrug, endDate);

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

    public Set<DayOfWeek> getPlannedDaysOfWeek(UserDrug userDrug) {
        return userDrug.getDrugDoseDays().stream()
                .map(DrugDoseDay::getDay)
                .map(Days::toDayOfWeek)
                .sorted(Comparator.comparingInt(DayOfWeek::getValue))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private LocalDate findNextPlannedDate(LocalDate date, Set<DayOfWeek> plannedDaysOfWeek) {
        DayOfWeek currentDayOfWeek = date.getDayOfWeek();
        TreeSet<DayOfWeek> sortedPlannedDays = new TreeSet<>(plannedDaysOfWeek);

        for (DayOfWeek plannedDay : sortedPlannedDays) {
            if (currentDayOfWeek.getValue() <= plannedDay.getValue()) {
                return date.with(TemporalAdjusters.nextOrSame(plannedDay));
            }
        }

        return date.with(TemporalAdjusters.next(sortedPlannedDays.first()));
    }

    private LocalDate getEndDate(UserDrug userDrug, LocalDate endDate) {
        if (userDrug.getEndDate() == null) {
            return endDate;
        } else {
            return endDate.isBefore(userDrug.getEndDate()) ? endDate : userDrug.getEndDate();
        }
    }
}
