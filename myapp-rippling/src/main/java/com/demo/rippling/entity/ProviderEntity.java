package com.demo.rippling.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "provider")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ProviderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "provider_id")
    private Long providerId;

    @Column(nullable = false)
    private String name;

    private Integer zip;

    @Column(name = "late_appointments")
    private Integer lateAppointments;

    @Column(name = "client_retention", precision = 4, scale = 2)
    private BigDecimal clientRetention;

    private Integer age;

    @OneToMany(mappedBy = "provider", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ProviderScheduleEntity> schedules = new ArrayList<>();
}
