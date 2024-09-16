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

    public UserResponse extendedMap(User user) {
        UserResponse userResponse = map(user);
        userResponse.setLastName(user.getLastName());
        userResponse.setPhoneNumber(user.getPhoneNumber());
        userResponse.setSex(user.getSex());
        userResponse.setBirthDate(user.getBirthDate());
        userResponse.setHeight(user.getHeight());

        return userResponse;
    }
}
