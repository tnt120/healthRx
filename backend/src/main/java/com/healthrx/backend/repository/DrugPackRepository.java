package com.healthrx.backend.repository;

import com.healthrx.backend.api.internal.DrugPack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DrugPackRepository extends JpaRepository<DrugPack, Integer> {

    @Query("SELECT dp.packUnit FROM DrugPack dp WHERE dp.drug.id = :drugId")
    List<String> findPackUnitByDrugId(
            @Param("drugId") Integer drugId
    );

    List<DrugPack> findAllByDrugId(Integer drugId);
}
