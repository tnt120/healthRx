package com.healthrx.backend.api.internal;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalTime;

@Entity
@Table(name = "accounts_settings")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Accessors(chain = true)
public class AccountSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(columnDefinition = "time")
    private LocalTime parametersNotifications;
    @Column(columnDefinition = "boolean default false")
    private boolean isBadResultsNotificationsEnabled;
    @Column(columnDefinition = "boolean default false")
    private boolean isDrugNotificationsEnabled;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
}
