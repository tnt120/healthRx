package com.healthrx.backend.api.internal;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

@Entity
@Table(name = "drugs")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Accessors(chain = true)
public class Drug {
    @Id
    private Integer id;

    @Column(length = 512)
    private String name;

    @Column(length = 512)
    private String power;

    @Column(length = 512)
    private String pharmaceuticalFormName;

    @Column(length = 512)
    private String company;

    @Column(length = 512)
    private String procedureType;

    @Column(length = 512)
    private String permitNumber;

    @Column(length = 512)
    private String permitExpiration;

    @Column(length = 1024)
    private String info;

    @Column(length = 1024)
    private String characteristic;

//    private String atcCode;

//    @OneToMany(mappedBy = "drug", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    private List<DrugPack> drugPacks;
}
