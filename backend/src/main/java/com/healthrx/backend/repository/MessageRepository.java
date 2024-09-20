package com.healthrx.backend.repository;

import com.healthrx.backend.api.internal.chat.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, String> {
}
