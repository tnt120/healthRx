package com.healthrx.backend.repository;

import com.healthrx.backend.api.internal.chat.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface MessageRepository extends JpaRepository<Message, String>, JpaSpecificationExecutor<Message> {
    void deleteAllByFriendshipId(String friendshipId);
}
