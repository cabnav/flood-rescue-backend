package com.floodrescue.backend.rescue.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Entity
@Table(name = "team_positions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeamPosition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "position_id")
    private Integer positionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private RescueTeam team;

    @Column(name = "latitude", nullable = false)
    private Integer latitude;

    @Column(name = "longitude", nullable = false)
    private Integer longitude;

    @Column(name = "recorded_at")
    private LocalTime recordedAt;
}
