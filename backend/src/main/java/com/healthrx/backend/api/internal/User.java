package com.healthrx.backend.api.internal;

import com.healthrx.backend.api.internal.enums.Role;
import com.healthrx.backend.api.internal.enums.Sex;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.OffsetDateTime;

@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Accessors(chain = true)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(unique = true)
    private String email;
    private String password;
    private String pictureUrl;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private OffsetDateTime birthDate;
    private Double weight;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
    private Sex sex;

    private Boolean configured;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private DoctorDetails doctorDetails;
}
