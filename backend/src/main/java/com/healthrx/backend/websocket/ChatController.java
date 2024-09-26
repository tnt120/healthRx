package com.healthrx.backend.websocket;

import com.healthrx.backend.api.external.chat.ChatMessageDTO;
import com.healthrx.backend.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @MessageMapping("/chat")
    public void processMessage(@Payload ChatMessageDTO chatMessageDTO) {
        chatService.sendChatMessage(chatMessageDTO);
    }
}
