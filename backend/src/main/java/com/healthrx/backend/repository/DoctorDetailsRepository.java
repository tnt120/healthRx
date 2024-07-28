package com.healthrx.backend.repository;

import com.healthrx.backend.api.internal.DoctorDetails;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DoctorDetailsRepository extends JpaRepository<DoctorDetails, String> {
}
