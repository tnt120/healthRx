package com.healthrx.backend.mapper;

import com.healthrx.backend.api.external.DrugPackResponse;
import com.healthrx.backend.api.external.DrugResponse;
import com.healthrx.backend.api.external.UserDrugMonitorResponse;
import com.healthrx.backend.api.internal.Drug;
import com.healthrx.backend.api.internal.DrugPack;
import com.healthrx.backend.api.internal.UserDrug;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;

@Component
public class DrugMapper {

    public DrugResponse map(Drug drug, String unit) {
        return DrugResponse.builder()
                .id(drug.getId())
                .atcCodes(
                        drug.getAtcCodes() == null ? Collections.emptyList()
                        : Arrays.stream(drug.getAtcCodes().split(",")).toList())
                .name(drug.getName())
                .power(Objects.equals(drug.getPower(), "-") ? "" : drug.getPower())
                .pharmaceuticalFormName(drug.getPharmaceuticalFormName())
                .unit(unit)
                .build();
    }

    public UserDrugMonitorResponse map(UserDrug userDrug, String unit, LocalTime time, LocalTime takenTime) {
        return UserDrugMonitorResponse.builder()
                .id(userDrug.getId())
                .drug(map(userDrug.getDrug(), unit))
                .doseSize(userDrug.getDoseSize())
                .priority(userDrug.getPriority())
                .time(time)
                .takenTime(takenTime)
                .build();
    }

    public DrugPackResponse map(DrugPack drugPack, Integer packSize) {
        return DrugPackResponse.builder()
                .id(drugPack.getId())
                .gtinCode(drugPack.getGtinCode())
                .accessibilityCategory(drugPack.getAccessibilityCategory())
                .packType(drugPack.getPackType())
                .packSize(packSize)
                .packUnit(drugPack.getPackUnit())
                .build();
    }
}
