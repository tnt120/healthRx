package com.healthrx.backend.service.impl;

import com.healthrx.backend.api.external.chat.ChatMessageDTO;
import com.healthrx.backend.api.internal.chat.Message;
import com.healthrx.backend.mapper.MessageMapper;
import com.healthrx.backend.repository.FriendshipRepository;
import com.healthrx.backend.repository.MessageRepository;
import com.healthrx.backend.repository.UserRepository;
import com.healthrx.backend.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import static com.healthrx.backend.handler.BusinessErrorCodes.FRIENDSHIP_NOT_FOUND;
import static com.healthrx.backend.handler.BusinessErrorCodes.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatServiceImpl implements ChatService {

    private final SimpMessagingTemplate messagingTemplate;
    private final UserRepository userRepository;
    private final FriendshipRepository friendshipRepository;
    private final MessageRepository messageRepository;
    private final MessageMapper messageMapper;

    public void sendChatMessage(ChatMessageDTO chatMessageDTO) {
        Message message = messageRepository.save(
                Message.builder()
                        .content(chatMessageDTO.getContent())
                        .receiver(userRepository.findById(chatMessageDTO.getReceiverId())
                                .orElseThrow(USER_NOT_FOUND::getError))
                        .sender(userRepository.findById(chatMessageDTO.getSenderId())
                                .orElseThrow(USER_NOT_FOUND::getError))
                        .friendship(friendshipRepository.findById(chatMessageDTO.getFriendshipId())
                                .orElseThrow(FRIENDSHIP_NOT_FOUND::getError))
                        .isDelivered(false)
                        .isRead(false)
                        .build()
        );

        messagingTemplate.convertAndSendToUser(
                chatMessageDTO.getReceiverId(),
                "/queue/messages",
                messageMapper.map(message)
        );
    }
}
