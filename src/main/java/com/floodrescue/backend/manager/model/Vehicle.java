package com.floodrescue.backend.manager.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "vehicles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vehicle_id")
    private Integer vehicleId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "depot_id")
    private VehicleDepot depot;

    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "model")
    private String model;

    @Column(name = "license_plate")
    private String licensePlate;

    @Column(name = "capacity_person")
    private Integer capacityPerson;

    @Column(name = "status", nullable = false)
    private String status;
}
