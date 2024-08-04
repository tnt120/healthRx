package com.healthrx.backend.api.internal;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Entity
@Table(name = "drug_packs")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Accessors(chain = true)
public class DrugPack {

    @Id
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "drug_id")
    @JsonBackReference
    private Drug drug;

    private String gtinCode;
    private String accessibilityCategory;
    private String packType;
    private String packSize;
    private String packagesQuantity;
    private String packUnit;
}
