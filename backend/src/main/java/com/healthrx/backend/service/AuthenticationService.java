package com.healthrx.backend.service;

import com.healthrx.backend.api.external.AuthRequest;

public interface AuthenticationService {
    void register(AuthRequest request);
}
