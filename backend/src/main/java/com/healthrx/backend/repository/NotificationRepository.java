package com.healthrx.backend.repository;

import com.healthrx.backend.api.internal.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, String> {
    List<Notification> findAllByUserId(String userId);
    void deleteAllByUserIdAndIdIn(String userId, List<String> notificationIds);
}
