package com.healthrx.backend.mapper;

import com.healthrx.backend.api.external.UserDrugsRequest;
import com.healthrx.backend.api.external.UserDrugsResponse;
import com.healthrx.backend.api.internal.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserDrugMapper {
    private final DrugMapper drugMapper;

    public UserDrugsResponse map(UserDrug userDrug, String drugUnit) {
        return UserDrugsResponse.builder()
                .id(userDrug.getId())
                .drug(drugMapper.map(userDrug.getDrug(), drugUnit))
                .doseSize(userDrug.getDoseSize())
                .priority(userDrug.getPriority())
                .startDate(userDrug.getStartDate())
                .endDate(userDrug.getEndDate())
                .amount(userDrug.getAmount())
                .doseTimes(userDrug.getDrugDoseTimes().stream().map(DrugDoseTime::getDoseTime).toList())
                .doseDays(userDrug.getDrugDoseDays().stream().map(DrugDoseDay::getDay).toList())
                .build();
    }

    public UserDrug map(UserDrugsRequest userDrugsResponse, Drug drug, User user) {
        return UserDrug.builder()
                .user(user)
                .drug(drug)
                .doseSize(userDrugsResponse.getDoseSize())
                .priority(userDrugsResponse.getPriority())
                .startDate(userDrugsResponse.getStartDate())
                .endDate(userDrugsResponse.getEndDate())
                .amount(userDrugsResponse.getAmount())
                .build();
    }
}
