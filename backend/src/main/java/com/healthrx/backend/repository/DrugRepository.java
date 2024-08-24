package com.healthrx.backend.repository;

import com.healthrx.backend.api.internal.Drug;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface DrugRepository extends JpaRepository<Drug, Integer>, JpaSpecificationExecutor<Drug> {
}
