package com.healthrx.backend.mapper;

import com.healthrx.backend.api.external.UnitDTO;
import com.healthrx.backend.api.internal.Unit;
import org.springframework.stereotype.Component;

@Component
public class UnitMapper {
    public UnitDTO map(Unit unit) {
        return UnitDTO.builder()
                .id(unit.getId())
                .name(unit.getName())
                .symbol(unit.getSymbol())
                .build();
    }
}
