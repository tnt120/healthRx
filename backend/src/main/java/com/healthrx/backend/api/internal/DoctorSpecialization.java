package com.healthrx.backend.api.internal;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Entity
@Table(name = "doctor_specializations")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Accessors(chain = true)
public class DoctorSpecialization {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "doctor_details_id")
    private DoctorDetails doctorDetails;

    @ManyToOne
    @JoinColumn(name = "specialization_id")
    private Specialization specialization;
}
