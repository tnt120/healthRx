package com.healthrx.backend.repository;

import com.healthrx.backend.api.internal.Parameter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ParameterRepository extends JpaRepository<Parameter, String> {
    Optional<Parameter> findByName(String name);
}
