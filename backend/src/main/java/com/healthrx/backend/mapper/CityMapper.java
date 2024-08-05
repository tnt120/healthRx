package com.healthrx.backend.mapper;

import com.healthrx.backend.api.external.CityDTO;
import com.healthrx.backend.api.internal.City;
import org.springframework.stereotype.Component;

@Component
public class CityMapper {

    public CityDTO map(City city) {
        return CityDTO.builder()
                .id(city.getId())
                .name(city.getName())
                .build();
    }
}
