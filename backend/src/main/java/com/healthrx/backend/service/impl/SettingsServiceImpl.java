package com.healthrx.backend.service.impl;

import com.healthrx.backend.api.external.settings.PasswordChangeRequest;
import com.healthrx.backend.api.external.settings.PersonalDataChangeRequest;
import com.healthrx.backend.api.internal.User;
import com.healthrx.backend.repository.UserRepository;
import com.healthrx.backend.service.SettingsService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
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
    @Transactional
    public Void personalDataChange(PersonalDataChangeRequest request) {
        User user = principalSupplier.get();

        Optional.ofNullable(request.getFirstName()).ifPresent(user::setFirstName);

        Optional.ofNullable(request.getLastName()).ifPresent(user::setLastName);

        if (request.getEmail() != null) {
            if (request.getEmail().equals(user.getEmail()) || userRepository.existsByEmail(request.getEmail())) {
                throw EMAIL_ALREADY_EXISTS.getError();
            }

            user.setEmail(request.getEmail());
        }

        Optional.ofNullable(request.getSex()).ifPresent(user::setSex);

        Optional.ofNullable(request.getBirthDate()).ifPresent(user::setBirthDate);

        Optional.ofNullable(request.getPhoneNumber()).ifPresent(user::setPhoneNumber);

        if (request.getIsHeightChanged() != null && request.getIsHeightChanged()) {
            user.setHeight(request.getHeight());
        }

        userRepository.save(user);

        return null;
    }

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
