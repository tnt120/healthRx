package com.healthrx.backend.repository;

import com.healthrx.backend.api.internal.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, String> {
}
