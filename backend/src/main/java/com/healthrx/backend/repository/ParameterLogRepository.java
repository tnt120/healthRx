package com.healthrx.backend.repository;

import com.healthrx.backend.api.internal.ParameterLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ParameterLogRepository extends JpaRepository<ParameterLog, String> {

    @Query("SELECT pl.value FROM ParameterLog pl  WHERE pl.parameter.id = :parameterId AND pl.user.id = :userId AND FUNCTION('DATE', pl.createdAt) = CURRENT_DATE")
    Optional<Double> findParameterLogValueByParameterIdAndUserIdAndToday(
            @Param("parameterId") String parameterId,
            @Param("userId") String userId
    );

    @Query("SELECT pl FROM ParameterLog pl  WHERE pl.parameter.id = :parameterId AND pl.user.id = :userId AND FUNCTION('DATE', pl.createdAt) = CURRENT_DATE")
    Optional<ParameterLog> findParameterLogByParameterIdAndUserIdAndToday(
            @Param("parameterId") String parameterId,
            @Param("userId") String userId
    );
}
