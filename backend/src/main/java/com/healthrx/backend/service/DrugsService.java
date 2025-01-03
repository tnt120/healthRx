package com.healthrx.backend.service;

import com.healthrx.backend.api.external.*;

import java.util.List;

public interface DrugsService {
    PageResponse<DrugResponse> getAllDrugs(Integer page, Integer size, String sortBy, String order, String name);
    List<DrugResponse> getDrugsFromUser();
    DrugPacksResponse getDrugPacks(Integer id);
    PageResponse<UserDrugsResponse> getUserDrugs(Integer page, Integer size, String sortBy, String order);
    List<UserDrugsResponse> getAllUserDrugs(String userId);
    List<UserDrugMonitorResponse> getUserDrugMonitor();
    UserDrugMonitorResponse setUserDrugMonitor(UserDrugMonitorRequest userDrugMonitorRequest);
    UserDrugMonitorResponse editUserDrugMonitor(UserDrugMonitorRequest userDrugMonitorRequest);
    Void deleteUserDrugMonitor(Integer drugId, String time);
    UserDrugsResponse addUserDrug(UserDrugsRequest userDrugsRequest);
    UserDrugsResponse editUserDrug(UserDrugsRequest userDrugsRequest, String userDrugId);
    Void deleteUserDrug(String id);
}
