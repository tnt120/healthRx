package com.healthrx.backend.repository;

import com.healthrx.backend.api.internal.chat.Friendship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface FriendshipRepository extends JpaRepository<Friendship, String>, JpaSpecificationExecutor<Friendship> {
    Optional<Friendship> getFriendshipByUserIdAndDoctorId(String userId, String doctorId);
    List<Friendship> getFriendshipsByUserId(String userId);
    List<Friendship> getFriendshipsByDoctorId(String doctorId);
}
