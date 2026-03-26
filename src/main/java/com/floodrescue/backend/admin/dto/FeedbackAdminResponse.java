package com.floodrescue.backend.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackAdminResponse {
    private Integer id;
    private Integer requestId;
    private String userName;
    private String comment;
    private LocalDateTime createdAt;
}
