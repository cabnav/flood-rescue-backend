package com.floodrescue.backend.manager.model;

import com.floodrescue.backend.auth.model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "warehouses")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Warehouse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "resource_id")
    private String resourceId;

    @Column(name = "supply_id")
    private String supplyId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private WarehouseStatus status;

    @Column(name = "latitude", nullable = false, precision = 10, scale = 8)
    private BigDecimal latitude;

    @Column(name = "longitude", nullable = false, precision = 11, scale = 8)
    private BigDecimal longitude;

    @Column(name = "address", nullable = false)
    private String address;

    public enum WarehouseStatus {
        ACTIVE,
        INACTIVE,
        LOCKED
    }
}