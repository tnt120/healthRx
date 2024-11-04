package com.healthrx.backend.repository;

import com.healthrx.backend.api.internal.DoctorDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DoctorDetailsRepository extends JpaRepository<DoctorDetails, String> {
    Optional<DoctorDetails> findByUserId(String userId);
    void deleteByUserId(String userId);
    Integer countAllByIsVerifiedDoctorIsFalseAndUnverifiedMessageIsNull();
}
