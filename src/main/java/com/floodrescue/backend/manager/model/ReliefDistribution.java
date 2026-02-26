package com.floodrescue.backend.manager.model;

import com.floodrescue.backend.auth.model.User;
import com.floodrescue.backend.rescue.model.Mission;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "relief_distributions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReliefDistribution {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mission_id", nullable = false)
    private Mission mission;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventory_id", nullable = false)
    private Inventory inventory;

    @Column(name = "quantity_distributed", nullable = false)
    private Integer quantityDistributed;

    @Column(name = "household_identifier")
    private String householdIdentifier;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recorded_by_id")
    private User recordedBy;

    @Column(name = "distributed_at", nullable = false)
    private LocalDateTime distributedAt = LocalDateTime.now();

    @Column(name = "is_confirmed", nullable = false)
    private Boolean isConfirmed = false;
}
