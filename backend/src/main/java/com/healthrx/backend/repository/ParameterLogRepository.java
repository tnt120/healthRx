package com.healthrx.backend.repository;

import com.healthrx.backend.api.internal.ParameterLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParameterLogRepository extends JpaRepository<ParameterLog, String> {
}
