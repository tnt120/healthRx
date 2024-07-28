package com.healthrx.backend.repository;

import com.healthrx.backend.api.internal.DoctorSpecialization;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DoctorSpecializationRepository extends JpaRepository<DoctorSpecialization, String> {
}
