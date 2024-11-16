package com.healthrx.backend.services;

import com.healthrx.backend.api.external.chat.ChatMessageDTO;
import com.healthrx.backend.api.external.chat.ConversationDTO;
import com.healthrx.backend.api.internal.User;
import com.healthrx.backend.api.internal.chat.Friendship;
import com.healthrx.backend.api.internal.chat.Message;
import com.healthrx.backend.api.internal.enums.FriendshipStatus;
import com.healthrx.backend.api.internal.enums.Role;
import com.healthrx.backend.mapper.FriendshipMapper;
import com.healthrx.backend.mapper.MessageMapper;
import com.healthrx.backend.repository.FriendshipRepository;
import com.healthrx.backend.repository.MessageRepository;
import com.healthrx.backend.repository.UserRepository;

import com.healthrx.backend.service.impl.ChatServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @Mock
    private UserRepository userRepository;

    @Mock
    private FriendshipRepository friendshipRepository;

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private MessageMapper messageMapper;

    @Mock
    private FriendshipMapper friendshipMapper;

    @Mock
    private Supplier<User> principalSupplier;

    @InjectMocks
    private ChatServiceImpl chatService;

    @Test
    void shouldMarkMessageAsRead() {
        User sender = new User().setId("1");
        User receiver = new User().setId("2");
        Message message = Message.builder()
                .id("m1")
                .receiver(receiver)
                .sender(sender)
                .isRead(false)
                .build();

        Mockito.when(messageRepository.findById("m1")).thenReturn(Optional.of(message));

        ChatMessageDTO expectedMessageDTO = ChatMessageDTO.builder()
                .id("m1")
                .isRead(true)
                .build();

        Mockito.when(messageMapper.map(message)).thenReturn(expectedMessageDTO);

        chatService.markMessageAsRead("m1", "2");

        Assertions.assertTrue(message.getIsRead());
        Mockito.verify(messageRepository).save(message);
        Mockito.verify(messagingTemplate).convertAndSendToUser(eq("1"), eq("/queue/messages/read"), eq(expectedMessageDTO));
    }

    @Test
    void shouldGetConversations() {
        User user = new User().setId("1").setRole(Role.USER);
        Mockito.when(principalSupplier.get()).thenReturn(user);

        Friendship friendship = new Friendship()
                .setId("f1")
                .setUser(user)
                .setDoctor(new User().setId("2"))
                .setStatus(FriendshipStatus.ACCEPTED);

        ConversationDTO expectedConversation = ConversationDTO.builder()
                .friendshipId("f1")
                .build();

        Mockito.when(friendshipRepository.findAll(any(Specification.class), any(Sort.class)))
                .thenReturn(List.of(friendship));
        Mockito.when(friendshipMapper.mapConversation(friendship, friendship.getDoctor()))
                .thenReturn(expectedConversation);

        List<ConversationDTO> result = chatService.getConversations();

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(expectedConversation, result.getFirst());
    }

    @Test
    void shouldGetMessages() {
        User user = new User().setId("1");
        Friendship friendship = new Friendship()
                .setId("f1")
                .setUser(user)
                .setDoctor(new User().setId("2"));

        Message message = Message.builder()
                .id("m1")
                .content("EncryptedMessage")
                .build();

        ChatMessageDTO expectedMessageDTO = ChatMessageDTO.builder()
                .id("m1")
                .content("DecryptedMessage")
                .build();

        Mockito.when(principalSupplier.get()).thenReturn(user);
        Mockito.when(friendshipRepository.findById("f1")).thenReturn(Optional.of(friendship));
        Mockito.when(messageRepository.findAll(any(Specification.class), any(Sort.class)))
                .thenReturn(List.of(message));
        Mockito.when(messageMapper.map(message)).thenReturn(expectedMessageDTO);

        List<ChatMessageDTO> result = chatService.getMessages("f1");

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(expectedMessageDTO, result.getFirst());
    }

    @Test
    void shouldReturnEmptyConversationsIfNoneExistForUser() {
        User user = new User().setId("1").setRole(Role.USER);
        Mockito.when(principalSupplier.get()).thenReturn(user);

        Mockito.when(friendshipRepository.findAll(Mockito.any(Specification.class), Mockito.any(Sort.class)))
                .thenReturn(List.of());

        List<ConversationDTO> result = chatService.getConversations();

        Assertions.assertNotNull(result, "Result should not be null");
        Assertions.assertTrue(result.isEmpty(), "Conversations list should be empty");
    }

    @Test
    void shouldReturnEmptyMessagesIfNoneExistForFriendship() {
        User user = new User().setId("1");
        Mockito.when(principalSupplier.get()).thenReturn(user);

        Friendship friendship = new Friendship()
                .setId("f1")
                .setUser(user)
                .setDoctor(new User().setId("2"));

        Mockito.when(friendshipRepository.findById("f1")).thenReturn(Optional.of(friendship));

        Mockito.when(messageRepository.findAll(Mockito.any(Specification.class), Mockito.any(Sort.class)))
                .thenReturn(List.of());

        List<ChatMessageDTO> result = chatService.getMessages("f1");

        Assertions.assertNotNull(result, "Result should not be null");
        Assertions.assertTrue(result.isEmpty(), "Messages list should be empty");
    }

}
