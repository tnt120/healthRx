package com.healthrx.backend.service;

import com.healthrx.backend.api.external.DrugResponse;
import com.healthrx.backend.api.external.PageResponse;

public interface DrugsService {
    PageResponse<DrugResponse> getAllDrugs(Integer page, Integer size, String sortBy, String order, String name);
}
