package com.healthrx.backend.security.service;

import com.healthrx.backend.api.internal.User;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.util.function.Supplier;

@Component
@RequestScope
public class UserProvider implements Supplier<User> {

    @Override
    public User get() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}