package com.healthrx.backend.api.internal;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

@Entity
@Table(name = "doctor_details")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Accessors(chain = true)
public class DoctorDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String numberPWZ;
    private String numberPESEL;

    @OneToOne(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private Image frontPwzCardImage;

    @OneToOne(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private Image backPwzCardImage;

    @ManyToOne
    @JoinColumn(name = "city_id")
    private City city;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "doctorDetails", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DoctorSpecialization> doctorSpecializations;
}
