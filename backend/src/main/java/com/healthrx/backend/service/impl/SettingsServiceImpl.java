package com.healthrx.backend.service.impl;

import com.healthrx.backend.api.external.settings.PasswordChangeRequest;
import com.healthrx.backend.api.internal.User;
import com.healthrx.backend.repository.UserRepository;
import com.healthrx.backend.service.SettingsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.function.Supplier;

import static com.healthrx.backend.handler.BusinessErrorCodes.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class SettingsServiceImpl implements SettingsService {
    private final Supplier<User> principalSupplier;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Override
    public Void passwordChange(PasswordChangeRequest request) {
        User user = principalSupplier.get();

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw INCORRECT_CURRENT_PASSWORD.getError();
        }

        if (!request.getNewPassword().equals(request.getNewPasswordConfirmation())) {
            throw NEW_PASSWORDS_NOT_MATCH.getError();
        }

        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw NO_DIFFERENT_NEW_PASSWORD.getError();
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));

        userRepository.save(user);

        log.info("Password changed for user: {}", user.getEmail());

        return null;
    }
}
