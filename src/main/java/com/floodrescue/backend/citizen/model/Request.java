package com.floodrescue.backend.citizen.model;

import com.floodrescue.backend.auth.model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "phone", nullable = false)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(name = "request_type", nullable = false)
    private RequestType requestType;

    @Column(name = "latitude", nullable = false, precision = 10, scale = 8)
    private BigDecimal latitude;

    @Column(name = "longitude", nullable = false, precision = 11, scale = 8)
    private BigDecimal longitude;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false)
    private Priority priority;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private RequestStatus status;

    @Column(name = "request_supplies")
    private String requestSupplies;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public enum RequestType {
        RESCUE,
        RELIEF
    }

    public enum Priority {
        CRITICAL,
        HIGH,
        MEDIUM,
        NORMAL
    }

    public enum RequestStatus {
        CREATED,
        IN_PROGRESS,
        COMPLETED,
        CANCELLED
    }
    @OneToMany(mappedBy = "request",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<RequestMedia> medias;
}
