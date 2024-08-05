package com.healthrx.backend.repository;

import com.healthrx.backend.api.internal.City;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CityRepository extends JpaRepository<City, String> {
}
