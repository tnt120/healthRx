package com.healthrx.backend.api.external.settings;

import com.healthrx.backend.api.internal.enums.Sex;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonalDataChangeRequest {
    private String firstName;
    private String lastName;
    private String email;
    private Sex sex;
    private LocalDate birthDate;
    private String phoneNumber;
    private Double height;
    private Boolean isHeightChanged;
}
