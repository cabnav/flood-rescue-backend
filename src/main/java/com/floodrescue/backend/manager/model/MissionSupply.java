package com.floodrescue.backend.manager.model;

import com.floodrescue.backend.rescue.model.Mission;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "mission_supplies")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MissionSupply {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mission_id", nullable = false)
    private Mission mission;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventory_id", nullable = false)
    private Inventory inventory;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    /**
     * Đã hoàn về kho chưa (khi mission FAILED/CANCELLED).
     * Tránh hoàn trùng nhiều lần.
     */
    @Column(name = "returned", nullable = false)
    private Boolean returned = false;
}
