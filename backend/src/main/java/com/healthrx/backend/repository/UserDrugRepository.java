package com.healthrx.backend.repository;

import com.healthrx.backend.api.internal.UserDrug;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserDrugRepository extends JpaRepository<UserDrug, String> {
    @Query("SELECT ud FROM UserDrug ud  WHERE ud.user.id = :userId AND ud.drug.id = :drugId")
    Optional<UserDrug> findUserDrugByUserIdAndDrugId(
            @Param("drugId") Integer drugId,
            @Param("userId") String userId
    );
}
