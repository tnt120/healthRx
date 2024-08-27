package com.healthrx.backend.service;

import com.healthrx.backend.api.external.DrugResponse;
import com.healthrx.backend.api.external.PageResponse;
import com.healthrx.backend.api.external.UserDrugsRequest;
import com.healthrx.backend.api.external.UserDrugsResponse;

public interface DrugsService {
    PageResponse<DrugResponse> getAllDrugs(Integer page, Integer size, String sortBy, String order, String name);
    PageResponse<UserDrugsResponse> getUserDrugs(Integer page, Integer size, String sortBy, String order);
    UserDrugsResponse addUserDrug(UserDrugsRequest userDrugsRequest);
    Void deleteUserDrug(String id);
}
