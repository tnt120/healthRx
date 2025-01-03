package com.healthrx.backend.repository;

import com.healthrx.backend.api.internal.DoctorSpecialization;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DoctorSpecializationRepository extends JpaRepository<DoctorSpecialization, String> {
    List<DoctorSpecialization> findAllByDoctorDetailsId(String doctorDetailsId);
    void deleteAllByDoctorDetailsId(String doctorDetailsId);
}
