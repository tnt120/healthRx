package com.healthrx.backend.api.internal;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Entity
@Table(name = "specializations")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Accessors(chain = true)
public class Specialization {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String name;
}
