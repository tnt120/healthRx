package com.healthrx.backend.mapper;

import com.healthrx.backend.api.external.FriendshipPermissions;
import com.healthrx.backend.api.external.UserResponse;
import com.healthrx.backend.api.external.chat.ConversationDTO;
import com.healthrx.backend.api.external.invitation.FriendshipResponse;
import com.healthrx.backend.api.internal.User;
import com.healthrx.backend.api.internal.chat.Friendship;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FriendshipMapper {
    private final MessageMapper messageMapper;

    public UserResponse mapUser(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .pictureUrl(user.getPictureUrl())
                .build();
    }

    public FriendshipPermissions mapPermissions(Friendship friendship) {
        return FriendshipPermissions.builder()
                .userMedicineAccess(friendship.getUserMedicineAccess())
                .activitiesAccess(friendship.getActivitiesAccess())
                .parametersAccess(friendship.getParametersAccess())
                .build();
    }

    public FriendshipResponse mapFriendship(Friendship friendship, User user) {
        return FriendshipResponse.builder()
                .friendshipId(friendship.getId())
                .user(mapUser(user))
                .status(friendship.getStatus())
                .updatedAt(friendship.getUpdatedAt())
                .permissions(mapPermissions(friendship))
                .build();
    }

    public ConversationDTO mapConversation(Friendship friendship, User user) {
        return ConversationDTO.builder()
                .friendshipId(friendship.getId())
                .user(mapUser(user))
                .lastMessage(friendship.getLastMessage() == null ? null : messageMapper.map(friendship.getLastMessage()))
                .build();
    }
}
