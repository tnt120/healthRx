package com.healthrx.backend.controller;

import com.healthrx.backend.api.external.chat.ChatMessageDTO;
import com.healthrx.backend.api.external.chat.ConversationDTO;
import com.healthrx.backend.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;

    @MessageMapping("/chat")
    public void processMessage(@Payload ChatMessageDTO chatMessageDTO) {
        chatService.sendChatMessage(chatMessageDTO);
    }

    @MessageMapping("/chat/read")
    public void processReadMessage(@Payload ChatMessageDTO chatMessageDTO) {
        chatService.markMessageAsRead(chatMessageDTO.getId(), chatMessageDTO.getReceiverId());
    }

    @GetMapping("/conversations")
    public ResponseEntity<List<ConversationDTO>> getConversations() {
        return ResponseEntity.ok(chatService.getConversations());
    }

    @GetMapping("/messages/{friendshipId}")
    public ResponseEntity<List<ChatMessageDTO>> getMessages(@PathVariable String friendshipId) {
        return ResponseEntity.ok(chatService.getMessages(friendshipId));
    }
}
