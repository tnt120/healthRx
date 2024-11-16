package com.healthrx.backend.services;

import com.healthrx.backend.api.external.FriendshipPermissions;
import com.healthrx.backend.api.external.invitation.FriendshipResponse;
import com.healthrx.backend.api.internal.User;
import com.healthrx.backend.api.internal.chat.Friendship;
import com.healthrx.backend.api.internal.enums.FriendshipStatus;
import com.healthrx.backend.api.internal.enums.Role;
import com.healthrx.backend.mapper.FriendshipMapper;
import com.healthrx.backend.repository.FriendshipRepository;
import com.healthrx.backend.security.service.UserProvider;
import com.healthrx.backend.service.impl.FriendshipServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FriendshipServiceTest {

    @Mock
    private FriendshipRepository friendshipRepository;

    @Mock
    private UserProvider principalSupplier;

    @Mock
    private FriendshipMapper friendshipMapper;

    @InjectMocks
    private FriendshipServiceImpl friendshipService;

    private User user;
    private User doctor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = User.builder().id("1").role(Role.USER).build();
        doctor = User.builder().id("2").role(Role.DOCTOR).build();
    }

    @Test
    void shouldGetFriendshipsForUser() {
        Friendship friendship = Friendship.builder()
                .id("f1")
                .user(user)
                .doctor(doctor)
                .status(FriendshipStatus.ACCEPTED)
                .updatedAt(LocalDateTime.now())
                .build();
        FriendshipResponse friendshipResponse = FriendshipResponse.builder()
                .friendshipId("f1")
                .status(FriendshipStatus.ACCEPTED)
                .build();

        when(principalSupplier.get()).thenReturn(user);
        when(friendshipRepository.getFriendshipsByUserId(user.getId())).thenReturn(List.of(friendship));
        when(friendshipMapper.mapUser(doctor)).thenReturn(null); // Map doctor to a UserResponse
        when(friendshipMapper.mapPermissions(friendship)).thenReturn(new FriendshipPermissions());
        when(friendshipMapper.mapFriendship(friendship, doctor)).thenReturn(friendshipResponse);

        List<FriendshipResponse> result = friendshipService.getFriendships(FriendshipStatus.ACCEPTED);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("f1", result.get(0).getFriendshipId());
        verify(friendshipRepository, times(1)).getFriendshipsByUserId(user.getId());
    }

    @Test
    void shouldReturnEmptyFriendshipsForUser() {
        when(principalSupplier.get()).thenReturn(user);
        when(friendshipRepository.getFriendshipsByUserId(user.getId())).thenReturn(Collections.emptyList());

        List<FriendshipResponse> result = friendshipService.getFriendships(FriendshipStatus.ACCEPTED);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(friendshipRepository, times(1)).getFriendshipsByUserId(user.getId());
    }

    @Test
    void shouldGetFriendshipsForDoctor() {
        Friendship friendship = Friendship.builder()
                .id("f2")
                .user(user)
                .doctor(doctor)
                .status(FriendshipStatus.WAITING)
                .updatedAt(LocalDateTime.now())
                .build();
        FriendshipResponse friendshipResponse = FriendshipResponse.builder()
                .friendshipId("f2")
                .status(FriendshipStatus.WAITING)
                .build();

        when(principalSupplier.get()).thenReturn(doctor);
        when(friendshipRepository.getFriendshipsByDoctorId(doctor.getId())).thenReturn(List.of(friendship));
        when(friendshipMapper.mapUser(user)).thenReturn(null); // Map user to a UserResponse
        when(friendshipMapper.mapPermissions(friendship)).thenReturn(new FriendshipPermissions());
        when(friendshipMapper.mapFriendship(friendship, user)).thenReturn(friendshipResponse);

        List<FriendshipResponse> result = friendshipService.getFriendships(FriendshipStatus.WAITING);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("f2", result.get(0).getFriendshipId());
        verify(friendshipRepository, times(1)).getFriendshipsByDoctorId(doctor.getId());
    }

    @Test
    void shouldReturnEmptyFriendshipsForDoctor() {
        when(principalSupplier.get()).thenReturn(doctor);
        when(friendshipRepository.getFriendshipsByDoctorId(doctor.getId())).thenReturn(Collections.emptyList());

        List<FriendshipResponse> result = friendshipService.getFriendships(FriendshipStatus.REJECTED);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(friendshipRepository, times(1)).getFriendshipsByDoctorId(doctor.getId());
    }

    @Test
    void shouldThrowErrorIfUserNotPermitted() {
        User admin = User.builder().id("3").role(Role.ADMIN).build();
        when(principalSupplier.get()).thenReturn(admin);

        assertThrows(RuntimeException.class, () -> friendshipService.getFriendships(FriendshipStatus.ACCEPTED));
    }
}
