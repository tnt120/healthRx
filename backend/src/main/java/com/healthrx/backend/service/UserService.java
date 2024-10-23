package com.healthrx.backend.service;

import com.healthrx.backend.api.external.*;
import com.healthrx.backend.api.external.admin.ChangeRoleReqRes;
import com.healthrx.backend.api.external.admin.DeleteUserRequest;
import com.healthrx.backend.api.internal.User;
import com.healthrx.backend.api.internal.enums.Role;

public interface UserService {
    PageResponse<UserResponse> getUsers(Integer page, Integer size, Role role, String firstName, String lastName);
    ChangeRoleReqRes changeRole(String userId, ChangeRoleReqRes req);
    Void deleteUser(String userId, DeleteUserRequest req);
    User verifyUser(UserVerificationRequest request);
    VerificationDataResponse getVerificationData(Token request);
    InitAndConfigResponse getInitAndConfigData();
    User getUser(String userId);
}
