package com.healthrx.backend.service;

import com.healthrx.backend.api.external.notifications.DiscardNotificationsRequest;
import com.healthrx.backend.api.external.notifications.NotificationDTO;

import java.util.List;

public interface NotificationsService {
    List<NotificationDTO> getNotificationsAll();
    Void discardNotification(DiscardNotificationsRequest req);
    void sendNotification(NotificationDTO notificationDTO, String userId);
}
