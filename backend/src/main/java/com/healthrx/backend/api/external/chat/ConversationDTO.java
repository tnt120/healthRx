package com.healthrx.backend.api.external.chat;

import com.healthrx.backend.api.external.UserResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConversationDTO {
    private String friendshipId;
    private UserResponse user;
    private ChatMessageDTO lastMessage;
}
