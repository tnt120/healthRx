package com.healthrx.backend.controller;

import com.healthrx.backend.api.external.chat.ChatMessageDTO;
import com.healthrx.backend.api.external.chat.ConversationDTO;
import com.healthrx.backend.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
@Tag(name = "Chat controller", description = "Kontroler do pobierania konwersacji oraz wiadomości")
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
    @Operation(summary = "Pobranie konwersacji", description = "Pobranie konwersacji użytkownika")
    public ResponseEntity<List<ConversationDTO>> getConversations() {
        return ResponseEntity.ok(chatService.getConversations());
    }

    @GetMapping("/messages/{friendshipId}")
    @Operation(summary = "Pobranie wiadomości", description = "Pobranie wiadomości z konwersacji")
    public ResponseEntity<List<ChatMessageDTO>> getMessages(@PathVariable String friendshipId) {
        return ResponseEntity.ok(chatService.getMessages(friendshipId));
    }
}
