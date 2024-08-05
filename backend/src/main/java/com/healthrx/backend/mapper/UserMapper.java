package com.healthrx.backend.mapper;

import com.healthrx.backend.api.external.UserResponse;
import com.healthrx.backend.api.internal.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponse map(User user) {
        return UserResponse.builder()
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .role(user.getRole().name())
                .pictureUrl(user.getPictureUrl())
                .build();
    }
}
