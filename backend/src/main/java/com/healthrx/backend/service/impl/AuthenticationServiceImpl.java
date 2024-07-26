package com.healthrx.backend.service.impl;

import com.healthrx.backend.api.external.AuthRequest;
import com.healthrx.backend.api.external.Token;
import com.healthrx.backend.api.internal.User;
import com.healthrx.backend.api.internal.enums.Role;
import com.healthrx.backend.repository.UserRepository;
import com.healthrx.backend.security.service.JwtService;
import com.healthrx.backend.security.util.TokenType;
import com.healthrx.backend.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.healthrx.backend.handler.BusinessErrorCodes.ALREADY_EXISTS;
import static com.healthrx.backend.security.util.TokenType.VERIFICATION;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    public void register(AuthRequest request) {

        Role role;
        if (request.getIsDoctor()) {
            role = Role.DOCTOR;
        } else {
            role = Role.USER;
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw ALREADY_EXISTS.getError();
        }

        var user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .configured(false)
                .isVerifiedDoctor(false)
                .build();

        userRepository.save(user);

        var verificationToken = jwtService.generateToken(user, VERIFICATION); // potrzebne zwrócienie tokena do wysłania emaila
    }

    @Override
    public Token login(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        var user = userRepository.findUserByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        var accessToken = jwtService.generateToken(user, TokenType.ACCESS);
        var refreshToken = jwtService.generateToken(user, TokenType.REFRESH);

        return Token.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken).build();
    }
}
