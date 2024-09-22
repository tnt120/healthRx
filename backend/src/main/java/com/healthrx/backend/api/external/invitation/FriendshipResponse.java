package com.healthrx.backend.api.external.invitation;

import com.healthrx.backend.api.external.UserResponse;
import com.healthrx.backend.api.internal.enums.FriendshipStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FriendshipResponse {
    String friendshipId;
    UserResponse user;
    FriendshipStatus status;
    LocalDateTime updatedAt;
}
