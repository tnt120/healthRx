package com.healthrx.backend.service;

import com.healthrx.backend.api.external.InitAndConfigResponse;
import com.healthrx.backend.api.external.Token;
import com.healthrx.backend.api.external.UserVerificationRequest;
import com.healthrx.backend.api.external.VerificationDataResponse;
import com.healthrx.backend.api.internal.User;

public interface UserService {

    User verifyUser(UserVerificationRequest request);
    VerificationDataResponse getVerificationData(Token request);
    InitAndConfigResponse getInitAndConfigData();
    User getUser(String userId);
}
