package com.healthrx.backend.repository;

import com.healthrx.backend.api.internal.DrugPack;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DrugPackRepository extends JpaRepository<DrugPack, Integer> {
}
