package com.healthrx.backend.api.external.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDTO {
    private String id;
    private String senderId;
    private String receiverId;
    private String content;
    private String friendshipId;
    private String createdAt;
    private Boolean isRead;
    private Boolean isDelivered;
}
