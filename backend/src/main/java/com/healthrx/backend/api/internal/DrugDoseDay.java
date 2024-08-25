package com.healthrx.backend.api.internal;

import com.healthrx.backend.api.internal.enums.Days;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Entity
@Table(name = "drug_dose_day")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Accessors(chain = true)
public class DrugDoseDay {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "user_drug_id", nullable = false)
    private UserDrug userDrugs;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Days day;
}
