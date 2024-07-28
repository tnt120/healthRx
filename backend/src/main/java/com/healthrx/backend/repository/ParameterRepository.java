package com.healthrx.backend.repository;

import com.healthrx.backend.api.internal.Parameter;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParameterRepository extends JpaRepository<Parameter, String> {
}
