package com.healthrx.backend.repository;

import com.healthrx.backend.api.internal.DrugDoseTime;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DrugDoseTimeRepository extends JpaRepository<DrugDoseTime, String> {
}
