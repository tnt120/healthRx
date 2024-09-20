package com.healthrx.backend.repository;

import com.healthrx.backend.api.internal.chat.Friendship;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FriendshipRepository extends JpaRepository<Friendship, String> {
}
