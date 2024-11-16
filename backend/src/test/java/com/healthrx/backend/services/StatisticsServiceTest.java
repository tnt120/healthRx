package com.healthrx.backend.services;

import com.healthrx.backend.api.internal.DrugDoseDay;
import com.healthrx.backend.api.internal.DrugDoseTime;
import com.healthrx.backend.api.internal.DrugLog;
import com.healthrx.backend.api.internal.UserDrug;
import com.healthrx.backend.api.internal.enums.Days;
import com.healthrx.backend.handler.BusinessErrorCodes;
import com.healthrx.backend.handler.ExceptionResponse;
import com.healthrx.backend.utils.models.DoseTimeStatistics;
import com.healthrx.backend.utils.models.DrugDayStatistics;
import com.healthrx.backend.utils.statistics.BmiCalculator;
import com.healthrx.backend.utils.statistics.DayStatisticsCalculator;
import com.healthrx.backend.utils.statistics.TimeStatisticsCalculator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static com.healthrx.backend.handler.BusinessErrorCodes.WRONG_BMI_DATA;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class StatisticsServiceTest {

    private BmiCalculator bmiCalculator;
    private DayStatisticsCalculator dayStatisticsCalculator;
    private TimeStatisticsCalculator timeStatisticsCalculator;

    @BeforeEach
    void setUp() {
        bmiCalculator = new BmiCalculator();
        dayStatisticsCalculator = new DayStatisticsCalculator();
        timeStatisticsCalculator = new TimeStatisticsCalculator();
    }

    // Test for BMI Calculator
    @Test
    void shouldCalculateBMI() {
        // Arrange
        Double weight = 70.0; // in kilograms
        Double height = 175.0; // in centimeters

        // Act
        Double result = bmiCalculator.calculateBMI(weight, height);

        // Assert
        assertNotNull(result, "BMI should not be null");
        assertEquals(22.86, result, 0.01, "BMI should be approximately 22.86");
    }

    @Test
    void shouldHandleZeroHeightInBMI() {
        Double weight = 70.0;
        Double height = 0.0;

        ExceptionResponse exception = assertThrows(ExceptionResponse.class,
                () -> bmiCalculator.calculateBMI(weight, height),
                "BMI calculation should throw an ExceptionResponse for zero height");

        assertNotNull(exception, "Exception should not be null");
        assertEquals(BusinessErrorCodes.WRONG_BMI_DATA.getCode(), exception.getBusinessErrorCode().getCode(), "Error code should match WRONG_BMI_DATA");
        assertEquals(BusinessErrorCodes.WRONG_BMI_DATA.getDescription(), exception.getBusinessErrorCode().getDescription(), "Error message should match WRONG_BMI_DATA description");
    }


    // Test for Day Statistics Calculator
    @Test
    void shouldCalculateDayStatistics() {
        // Arrange
        List<DrugDoseTime> times = List.of(
                DrugDoseTime.builder().doseTime(LocalTime.of(8, 0)).build(),
                DrugDoseTime.builder().doseTime(LocalTime.of(20, 0)).build()
        );

        UserDrug userDrug = UserDrug.builder()
                .drugDoseTimes(times)
                .drugDoseDays(List.of(
                        DrugDoseDay.builder().day(Days.MONDAY).build(),
                        DrugDoseDay.builder().day(Days.WEDNESDAY).build()
                ))
                .startDate(LocalDate.of(2023, 11, 1))
                .endDate(LocalDate.of(2023, 11, 30))
                .build();

        List<DrugLog> logs = List.of(
                DrugLog.builder().createdAt(LocalDateTime.of(2023, 11, 6, 8, 0)).build(),
                DrugLog.builder().createdAt(LocalDateTime.of(2023, 11, 6, 20, 0)).build(),
                DrugLog.builder().createdAt(LocalDateTime.of(2023, 11, 8, 8, 0)).build()
        );

        // Act
        DrugDayStatistics result = dayStatisticsCalculator.calculateDayStatistics(
                userDrug,
                logs,
                LocalDateTime.of(2023, 11, 1, 0, 0),
                LocalDateTime.of(2023, 11, 30, 23, 59)
        );

        // Assert
        assertNotNull(result, "Day statistics should not be null");
        assertEquals(1, result.getTotalDaysTaken(), "Total days taken should be 1");
        assertEquals(1, result.getTotalDaysPartiallyTaken(), "Total days partially taken should be 1");
        assertEquals(7, result.getTotalDaysMissed(), "Total days missed should be 2");
    }

    @Test
    void shouldHandleNoLogsForDayStatistics() {
        // Arrange
        List<DrugDoseTime> times = List.of(
                DrugDoseTime.builder().doseTime(LocalTime.of(8, 0)).build(),
                DrugDoseTime.builder().doseTime(LocalTime.of(20, 0)).build()
        );

        UserDrug userDrug = UserDrug.builder()
                .drugDoseTimes(times)
                .drugDoseDays(List.of(
                        DrugDoseDay.builder().day(Days.MONDAY).build(),
                        DrugDoseDay.builder().day(Days.WEDNESDAY).build()
                ))
                .startDate(LocalDate.of(2023, 11, 1))
                .endDate(LocalDate.of(2023, 11, 30))
                .build();

        List<DrugLog> logs = List.of(); // No logs

        // Act
        DrugDayStatistics result = dayStatisticsCalculator.calculateDayStatistics(
                userDrug,
                logs,
                LocalDateTime.of(2023, 11, 1, 0, 0),
                LocalDateTime.of(2023, 11, 30, 23, 59)
        );

        // Assert
        assertNotNull(result, "Day statistics should not be null");
        assertEquals(0, result.getTotalDaysTaken(), "Total days taken should be 0");
        assertEquals(0, result.getTotalDaysPartiallyTaken(), "Total days partially taken should be 0");
        assertEquals(9, result.getTotalDaysMissed(), "Total days missed should be 8");
    }

    // Test for Time Statistics Calculator
    @Test
    void shouldCalculateTimeStatistics() {
        // Arrange
        List<DrugLog> logs = List.of(
                DrugLog.builder().time(LocalTime.of(8, 0)).takenTime(LocalTime.of(8, 10)).build(),
                DrugLog.builder().time(LocalTime.of(20, 0)).takenTime(LocalTime.of(20, 5)).build(),
                DrugLog.builder().time(LocalTime.of(8, 0)).takenTime(LocalTime.of(8, 20)).build()
        );

        // Act
        DoseTimeStatistics result = timeStatisticsCalculator.calculateTimeStatistics(logs);

        // Assert
        assertNotNull(result, "Time statistics should not be null");
        assertEquals(11.67, result.getAvgDelay(), 0.01, "Average delay should be approximately 11.67 minutes");
        assertEquals(66.67, result.getPunctualityPercentage(), 0.01,
                "Punctuality percentage should be approximately 66.67%");
    }

    @Test
    void shouldHandleNoLogsForTimeStatistics() {
        List<DrugLog> logs = List.of();

        DoseTimeStatistics result = timeStatisticsCalculator.calculateTimeStatistics(logs);

        assertTrue(Double.isNaN(result.getAvgDelay()));
        assertTrue(Double.isNaN(result.getPunctualityPercentage()));
    }
}
