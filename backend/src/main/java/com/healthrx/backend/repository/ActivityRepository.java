package com.healthrx.backend.repository;

import com.healthrx.backend.api.internal.Activity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ActivityRepository extends JpaRepository<Activity, String> {
    Optional<Activity> findByName(String name);
}
