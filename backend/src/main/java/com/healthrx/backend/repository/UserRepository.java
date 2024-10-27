package com.healthrx.backend.repository;

import com.healthrx.backend.api.internal.User;
import com.healthrx.backend.api.internal.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String>, JpaSpecificationExecutor<User> {
    Optional<User> findUserByEmail(String email);
    boolean existsByEmail(String email);
    Integer countAllByRole(Role role);
}
