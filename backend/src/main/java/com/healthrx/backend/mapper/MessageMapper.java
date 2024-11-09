package com.healthrx.backend.mapper;

import com.healthrx.backend.api.external.chat.ChatMessageDTO;
import com.healthrx.backend.api.internal.chat.Message;
import com.healthrx.backend.security.aes.AesHandler;
import org.springframework.stereotype.Component;

@Component
public class MessageMapper {
    public ChatMessageDTO map(Message message) {
        return ChatMessageDTO.builder()
                .id(message.getId())
                .senderId(message.getSender().getId())
                .receiverId(message.getReceiver().getId())
                .content(AesHandler.decryptString(message.getContent()))
                .friendshipId(message.getFriendship().getId())
                .createdAt(message.getCreatedAt().toString())
                .isDelivered(message.getIsDelivered())
                .isRead(message.getIsRead())
                .build();
    }
}
