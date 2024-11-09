package com.healthrx.backend.service.impl;

import com.healthrx.backend.api.external.chat.ChatMessageDTO;
import com.healthrx.backend.api.external.chat.ConversationDTO;
import com.healthrx.backend.api.internal.User;
import com.healthrx.backend.api.internal.chat.Friendship;
import com.healthrx.backend.api.internal.chat.Message;
import com.healthrx.backend.api.internal.enums.Role;
import com.healthrx.backend.mapper.FriendshipMapper;
import com.healthrx.backend.mapper.MessageMapper;
import com.healthrx.backend.repository.FriendshipRepository;
import com.healthrx.backend.repository.MessageRepository;
import com.healthrx.backend.repository.UserRepository;
import com.healthrx.backend.security.aes.AesHandler;
import com.healthrx.backend.service.ChatService;
import com.healthrx.backend.specification.FriendshipSpecification;
import com.healthrx.backend.specification.MessageSpecification;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Supplier;

import static com.healthrx.backend.handler.BusinessErrorCodes.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatServiceImpl implements ChatService {
    private final SimpMessagingTemplate messagingTemplate;
    private final UserRepository userRepository;
    private final FriendshipRepository friendshipRepository;
    private final MessageRepository messageRepository;
    private final MessageMapper messageMapper;
    private final FriendshipMapper friendshipMapper;
    private final Supplier<User> principalSupplier;

    public void sendChatMessage(ChatMessageDTO chatMessageDTO) {
        Friendship friendship = friendshipRepository.findById(chatMessageDTO.getFriendshipId())
                .orElseThrow(FRIENDSHIP_NOT_FOUND::getError);

        Message message = messageRepository.save(
                Message.builder()
                        .content(AesHandler.encryptString(chatMessageDTO.getContent()))
                        .receiver(userRepository.findById(chatMessageDTO.getReceiverId())
                                .orElseThrow(USER_NOT_FOUND::getError))
                        .sender(userRepository.findById(chatMessageDTO.getSenderId())
                                .orElseThrow(USER_NOT_FOUND::getError))
                        .friendship(friendship)
                        .isDelivered(false)
                        .isRead(false)
                        .build()
        );

        friendship.setLastMessage(message);

        friendshipRepository.save(friendship);

        messagingTemplate.convertAndSendToUser(
                chatMessageDTO.getSenderId(),
                "/queue/messages",
                messageMapper.map(message)
        );

        messagingTemplate.convertAndSendToUser(
                chatMessageDTO.getReceiverId(),
                "/queue/messages",
                messageMapper.map(message)
        );
    }

    @Override
    public void markMessageAsRead(String messageId, String receiverId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(MESSAGE_NOT_FOUND::getError);

        if (!message.getReceiver().getId().equals(receiverId)) {
            throw USER_NOT_PERMITTED.getError();
        }

        message.setIsRead(true);
        messageRepository.save(message);

        messagingTemplate.convertAndSendToUser(
                message.getSender().getId(),
                "/queue/messages/read",
                messageMapper.map(message)
        );
    }

    @Override
    @Transactional
    public List<ConversationDTO> getConversations() {
        User user = principalSupplier.get();

        Sort sort = Sort.by(Sort.Order.desc("lastMessage.createdAt").nullsLast());

        Specification<Friendship> specification = Specification.where(FriendshipSpecification.isAccepted());

        List<Friendship> friendships;
        List<ConversationDTO> conversations;

        if (user.getRole() == Role.USER) {
            specification = specification.and(FriendshipSpecification.userIdEquals(user.getId()));
            friendships = friendshipRepository.findAll(specification, sort);
            conversations = friendships.stream()
                    .map(friendship -> friendshipMapper.mapConversation(friendship, friendship.getDoctor()))
                    .toList();
        } else {
            specification = specification.and(FriendshipSpecification.doctorIdEquals(user.getId()));
            friendships = friendshipRepository.findAll(specification, sort);
            conversations = friendships.stream()
                    .map(friendship -> friendshipMapper.mapConversation(friendship, friendship.getUser()))
                    .toList();
        }

        return conversations;
    }

    @Override
    public List<ChatMessageDTO> getMessages(String friendshipId) {
        User user = principalSupplier.get();

        Friendship friendship = friendshipRepository.findById(friendshipId)
                .orElseThrow(FRIENDSHIP_NOT_FOUND::getError);

        if (!friendship.getUser().getId().equals(user.getId()) && !friendship.getDoctor().getId().equals(user.getId())) {
            throw USER_NOT_PERMITTED.getError();
        }

        Sort sort = Sort.by(Sort.Order.desc("createdAt").nullsLast());

        Specification<Message> spec = Specification.where(MessageSpecification.getMessagesByFriendshipId(friendshipId));

        return messageRepository.findAll(spec, sort).stream()
                .map(messageMapper::map)
                .toList();
    }
}
