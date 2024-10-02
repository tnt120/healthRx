package com.healthrx.backend.service;

import com.healthrx.backend.api.external.chat.ChatMessageDTO;
import com.healthrx.backend.api.external.chat.ConversationDTO;

import java.util.List;

public interface ChatService {
    void sendChatMessage(ChatMessageDTO chatMessageDTO);
    void markMessageAsRead(String messageId, String receiverId);
    List<ConversationDTO> getConversations();
    List<ChatMessageDTO> getMessages(String friendshipId);
}
