package com.healthrx.backend.service;

import com.healthrx.backend.api.external.LoginRequest;
import com.healthrx.backend.api.external.RegisterRequest;
import com.healthrx.backend.api.external.Token;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthenticationService {
    void register(RegisterRequest request);
    Token login(LoginRequest request);
    Token refresh(HttpServletRequest request, HttpServletResponse response);
}
