package com.healthrx.backend.repository;

import com.healthrx.backend.api.internal.DrugLog;
import com.healthrx.backend.api.internal.ParameterLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface DrugLogRepository extends JpaRepository<DrugLog, String> {
    @Query("SELECT dl FROM DrugLog dl WHERE dl.drug.id = :drugId AND dl.user.id = :userId AND dl.createdAt >= :startDate AND dl.createdAt <= :endDate ORDER BY dl.createdAt ASC")
    List<DrugLog> findDrugLogsByDrugIdAndUserIdAndDateRange(
            @Param("drugId") Integer drugId,
            @Param("userId") String userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT dl FROM DrugLog dl  WHERE dl.drug.id = :drugId AND dl.user.id = :userId AND dl.time = :time AND FUNCTION('DATE', dl.createdAt) = CURRENT_DATE")
    Optional<DrugLog> findDrugLogByDrugIdAndUserIdAndTimeToday(
            @Param("drugId") Integer drugId,
            @Param("userId") String userId,
            @Param("time") LocalTime time
    );

    void deleteAllByDrugId(Integer drugId);
    void deleteAllByUserId(String userId);
}
