package com.floodrescue.backend.admin.model;

import com.floodrescue.backend.auth.model.User;
import com.floodrescue.backend.citizen.model.Request;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "feedbacks", uniqueConstraints = @UniqueConstraint(columnNames = {"request_id", "user_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Feedback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id", nullable = false)
    private Request request;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Legacy DB compatibility only. CZ-04 uses isSafe + comment; rating is not part of the domain.
     * The feedbacks table still has a NOT NULL rating column. Set to 5 as a placeholder.
     * When migrating the DB to drop or relax this column, remove this field and the setRating(5) call in FeedbackServiceImpl.
     */
    @Column(name = "rating", nullable = false)
    private Integer rating = 5;

    @Column(name = "is_safe", nullable = false)
    private Boolean isSafe;

    @Column(name = "comment", nullable = false, columnDefinition = "TEXT")
    private String comment;

    @Enumerated(EnumType.STRING)
    @Column(name = "feedback_type", nullable = false)
    private FeedbackType feedbackType;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    /** Explicit setter for rating (Lombok may not generate). DB compatibility only; see field javadoc. */
    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public enum FeedbackType {
        RESCUE,
        RELIEF
    }
}