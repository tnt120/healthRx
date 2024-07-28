package com.healthrx.backend.repository;

import com.healthrx.backend.api.internal.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findUserByEmail(String email);
    boolean existsByEmail(String email);
}
