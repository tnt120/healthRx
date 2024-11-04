package com.healthrx.backend.api.external.notifications;

import com.healthrx.backend.api.internal.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {
    private String id;
    private NotificationType type;
    private String content;
    private String createdAt;
}
