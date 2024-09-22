package com.healthrx.backend.repository;

import com.healthrx.backend.api.internal.chat.Friendship;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FriendshipRepository extends JpaRepository<Friendship, String> {
    Optional<Friendship> getFriendshipByUserIdAndDoctorId(String userId, String doctorId);
    List<Friendship> getFriendshipsByUserId(String userId);
    List<Friendship> getFriendshipsByDoctorId(String doctorId);
}
