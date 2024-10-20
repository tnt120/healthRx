package com.healthrx.backend.repository;

import com.healthrx.backend.api.internal.UserParameter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserParameterRepository extends JpaRepository<UserParameter, String> {

    List<UserParameter> findAllByUserId(String userId);
    void deleteAllByParameterId(String parameterId);
}
