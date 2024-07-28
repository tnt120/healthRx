package com.healthrx.backend.repository;

import com.healthrx.backend.api.internal.AccountSettings;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountSettingsRepository extends JpaRepository<AccountSettings, String> {
}
