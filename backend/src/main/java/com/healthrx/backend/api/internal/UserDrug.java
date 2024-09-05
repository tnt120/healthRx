package com.healthrx.backend.api.internal;

import com.healthrx.backend.api.internal.enums.Priority;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "user_drugs")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Accessors(chain = true)
public class UserDrug {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "drug_id", nullable = false)
    private Drug drug;

    @Column(nullable = false)
    private Integer doseSize;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Priority priority;

    @Column(nullable = false)
    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private Integer amount;

    @OneToMany(mappedBy = "userDrugs", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    @JsonManagedReference
    private List<DrugDoseTime> drugDoseTimes;

    @OneToMany(mappedBy = "userDrugs", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    @JsonManagedReference
    private List<DrugDoseDay> drugDoseDays;
}
