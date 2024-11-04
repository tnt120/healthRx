package com.healthrx.backend.service.impl;

import com.healthrx.backend.api.external.notifications.DiscardNotificationsRequest;
import com.healthrx.backend.api.external.notifications.NotificationDTO;
import com.healthrx.backend.api.internal.Notification;
import com.healthrx.backend.api.internal.User;
import com.healthrx.backend.mapper.NotificationMapper;
import com.healthrx.backend.repository.NotificationRepository;
import com.healthrx.backend.repository.UserRepository;
import com.healthrx.backend.service.NotificationsService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Supplier;

import static com.healthrx.backend.handler.BusinessErrorCodes.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationsServiceImpl implements NotificationsService {
    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final Supplier<User> principalSupplier;
    private final UserRepository userRepository;

    @Override
    public List<NotificationDTO> getNotificationsAll() {
        User user = principalSupplier.get();
        return notificationRepository.findAllByUserId(user.getId()).stream()
                .map(notificationMapper::map)
                .toList();
    }

    @Override
    @Transactional
    public Void discardNotification(DiscardNotificationsRequest req) {
        User user = principalSupplier.get();

        notificationRepository.deleteAllByUserIdAndIdIn(user.getId(), req.getNotificationIds());

        return null;
    }

    @Override
    public void sendNotification(NotificationDTO notificationDTO, String userId) {
        User user = userRepository.findById(userId).orElseThrow(
                USER_NOT_FOUND::getError
        );

        Notification notification = notificationRepository.save(
                Notification.builder()
                        .type(notificationDTO.getType())
                        .content(notificationDTO.getContent())
                        .user(user)
                        .build()
        );

        messagingTemplate.convertAndSendToUser(
                user.getId(),
                "/queue/notifications",
                notificationMapper.map(notification)
        );
    }
}
