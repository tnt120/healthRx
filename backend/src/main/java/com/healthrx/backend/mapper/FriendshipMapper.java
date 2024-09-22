package com.healthrx.backend.mapper;

import com.healthrx.backend.api.external.FriendshipPermissions;
import com.healthrx.backend.api.external.UserResponse;
import com.healthrx.backend.api.internal.User;
import com.healthrx.backend.api.internal.chat.Friendship;
import org.springframework.stereotype.Component;

@Component
public class FriendshipMapper {

    public UserResponse mapUser(User user) {
        return UserResponse.builder()
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
}
