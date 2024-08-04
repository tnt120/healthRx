package com.healthrx.backend.repository;

import com.healthrx.backend.api.internal.Drug;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DrugRepository extends JpaRepository<Drug, Integer> {
}
