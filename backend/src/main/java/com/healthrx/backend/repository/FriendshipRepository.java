package com.healthrx.backend.repository;

import com.healthrx.backend.api.internal.chat.Friendship;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FriendshipRepository extends JpaRepository<Friendship, String> {
    Optional<Friendship> getFriendshipByUserIdAndDoctorId(String userId, String doctorId);
}
