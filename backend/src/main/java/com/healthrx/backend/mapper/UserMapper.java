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
                .build();
    }

    public UserResponse mapWithProfile(User user, byte[] profile) {
        UserResponse userResponse = map(user);
        userResponse.setPictureUrl(profile);
        return userResponse;
    }

    public UserResponse extendedMap(User user) {
        UserResponse userResponse = map(user);
        userResponse.setId(user.getId());
        userResponse.setLastName(user.getLastName());
        userResponse.setPhoneNumber(user.getPhoneNumber());
        userResponse.setSex(user.getSex());
        userResponse.setBirthDate(user.getBirthDate());
        userResponse.setHeight(user.getHeight());

        return userResponse;
    }
}
