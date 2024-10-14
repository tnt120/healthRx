package com.healthrx.backend.mapper;

import com.healthrx.backend.api.external.DoctorResponse;
import com.healthrx.backend.api.internal.DoctorDetails;
import com.healthrx.backend.api.internal.Specialization;
import com.healthrx.backend.api.internal.User;
import com.healthrx.backend.security.aes.AesHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DoctorMapper {
    private final CityMapper cityMapper;
    private final SpecializationMapper specializationMapper;

    public DoctorResponse map(User user, List<Specialization> specializations) {
        DoctorDetails doctorDetails = user.getDoctorDetails();

        return DoctorResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .specializations(specializations.stream().map(specializationMapper::map).toList())
                .city(cityMapper.map(doctorDetails.getCity()))
                .numberPWZ(doctorDetails.getNumberPWZ())
                .pictureUrl(user.getProfilePicture() != null ? AesHandler.decrypt(user.getProfilePicture().getContent()) : null)
                .build();
    }

    public DoctorResponse extendedVerifyMap(User user, List<Specialization> specializations, byte[] frontPwz, byte[] backPwz) {
        DoctorResponse doctorResponse = map(user, specializations);
        doctorResponse.setFrontPwz(frontPwz);
        doctorResponse.setBackPwz(backPwz);
        doctorResponse.setNumberPESEL(user.getDoctorDetails().getNumberPESEL());
        return doctorResponse;
    }
}
