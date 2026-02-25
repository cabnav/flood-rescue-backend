package com.floodrescue.backend.manager.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * AD-02: Danh mục loại phương tiện.
 * Chỉ Admin được CRUD. Áp dụng cho ca mới.
 */
@Entity
@Table(name = "vehicle_types")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "status", nullable = false)
    private String status = "ACTIVE";
}
