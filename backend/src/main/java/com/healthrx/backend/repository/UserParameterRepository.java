package com.healthrx.backend.repository;

import com.healthrx.backend.api.internal.UserParameter;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserParameterRepository extends JpaRepository<UserParameter, String> {
}
