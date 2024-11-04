package com.healthrx.backend.mapper;

import com.healthrx.backend.api.external.notifications.NotificationDTO;
import com.healthrx.backend.api.internal.Notification;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {
    public NotificationDTO map(Notification notification) {
        return NotificationDTO.builder()
                .id(notification.getId())
                .type(notification.getType())
                .content(notification.getContent())
                .createdAt(notification.getCreatedAt().toString())
                .build();
    }
}
