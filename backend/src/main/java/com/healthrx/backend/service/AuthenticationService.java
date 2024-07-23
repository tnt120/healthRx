package com.healthrx.backend.service;

import com.healthrx.backend.api.external.AuthRequest;
import com.healthrx.backend.api.external.Token;

public interface AuthenticationService {
    void register(AuthRequest request);
    Token login(AuthRequest request);
}
