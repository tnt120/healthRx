package com.healthrx.backend.mapper;

import com.healthrx.backend.api.external.DoctorResponse;
import com.healthrx.backend.api.internal.DoctorDetails;
import com.healthrx.backend.api.internal.Specialization;
import com.healthrx.backend.api.internal.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DoctorMapper {
    private final CityMapper cityMapper;
    private final SpecializationMapper specializationMapper;

    public DoctorResponse map(User user, DoctorDetails doctorDetails, List<Specialization> specializations) {
        return DoctorResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .specializations(specializations.stream().map(specializationMapper::map).toList())
                .city(cityMapper.map(doctorDetails.getCity()))
                .numberPWZ(doctorDetails.getNumberPWZ())
                .pictureUrl(user.getPictureUrl())
                .build();
    }
}
