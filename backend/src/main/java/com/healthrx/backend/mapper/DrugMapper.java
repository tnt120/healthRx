package com.healthrx.backend.mapper;

import com.healthrx.backend.api.external.DrugResponse;
import com.healthrx.backend.api.internal.Drug;
import org.springframework.stereotype.Component;

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
}
