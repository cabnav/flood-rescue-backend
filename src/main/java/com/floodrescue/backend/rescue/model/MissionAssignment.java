package com.floodrescue.backend.rescue.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "mission_assignments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MissionAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mission_id", nullable = false)
    private Mission mission;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rescue_team_id", nullable = false)
    private RescueTeam rescueTeam;

    @Column(name = "assigned_time")
    private LocalDateTime assignedTime;

    @Column(name = "mission_role")
    private String missionRole;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AssignmentStatus status = AssignmentStatus.PENDING;

    @Column(name = "decline_reason", columnDefinition = "TEXT")
    private String declineReason;

    @Column(name = "accepted_at")
    private LocalDateTime acceptedAt;

    @Column(name = "declined_at")
    private LocalDateTime declinedAt;

    public enum AssignmentStatus {
        PENDING,
        ACCEPTED,
        DECLINED
    }
}