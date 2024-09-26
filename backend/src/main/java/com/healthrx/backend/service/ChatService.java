package com.healthrx.backend.service;

import com.healthrx.backend.api.external.chat.ChatMessageDTO;

public interface ChatService {
    void sendChatMessage(ChatMessageDTO chatMessageDTO);
}
