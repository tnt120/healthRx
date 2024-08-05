package com.healthrx.backend.repository;

import com.healthrx.backend.api.internal.Activity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityRepository extends JpaRepository<Activity, String> {
}
