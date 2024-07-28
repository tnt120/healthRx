package com.healthrx.backend.repository;

import com.healthrx.backend.api.internal.Unit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UnitRepository extends JpaRepository<Unit, String> {
}
