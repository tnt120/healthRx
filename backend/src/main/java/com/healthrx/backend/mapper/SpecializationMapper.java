package com.healthrx.backend.mapper;

import com.healthrx.backend.api.external.SpecializationDTO;
import com.healthrx.backend.api.internal.Specialization;
import org.springframework.stereotype.Component;

@Component
public class SpecializationMapper {
    public SpecializationDTO map(Specialization specialization) {
        return SpecializationDTO.builder()
                .id(specialization.getId())
                .name(specialization.getName())
                .build();
    }
}
