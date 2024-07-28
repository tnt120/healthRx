package com.healthrx.backend.repository;

import com.healthrx.backend.api.internal.Specialization;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpecializationRepository extends JpaRepository<Specialization, String> {
}
