package com.floodrescue.backend.rescue.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "rescue_teams")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RescueTeam {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;
}
