package com.healthrx.backend.repository;

import com.healthrx.backend.api.internal.DrugLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalTime;
import java.util.Optional;

public interface DrugLogRepository extends JpaRepository<DrugLog, String> {
    @Query("SELECT dl FROM DrugLog dl  WHERE dl.drug.id = :drugId AND dl.user.id = :userId AND dl.time = :time AND FUNCTION('DATE', dl.createdAt) = CURRENT_DATE")
    Optional<DrugLog> findDrugLogByDrugIdAndUserIdAndTimeToday(
            @Param("drugId") Integer drugId,
            @Param("userId") String userId,
            @Param("time") LocalTime time
    );

    void deleteAllByDrugId(Integer drugId);
}
