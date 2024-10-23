package com.healthrx.backend.repository;

import com.healthrx.backend.api.internal.AccountSettings;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountSettingsRepository extends JpaRepository<AccountSettings, String> {
    Optional<AccountSettings> findAccountSettingsByUserId(String userId);
    void deleteByUserId(String userId);
}
