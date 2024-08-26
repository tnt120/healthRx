package com.healthrx.backend.repository;

import com.healthrx.backend.api.internal.DrugDoseDay;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DrugDoseDayRepository extends JpaRepository<DrugDoseDay, String> {
}
