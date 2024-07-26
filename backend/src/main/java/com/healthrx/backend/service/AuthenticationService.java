package com.healthrx.backend.service;

import com.healthrx.backend.api.external.AuthRequest;
import com.healthrx.backend.api.external.Token;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthenticationService {
    void register(AuthRequest request);
    Token login(AuthRequest request);
    Token refresh(HttpServletRequest request, HttpServletResponse response);
}
