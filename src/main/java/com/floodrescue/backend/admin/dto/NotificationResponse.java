package com.floodrescue.backend.admin.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {
    Integer id;
    Integer userId;
    String message;
    Boolean isRead;
    LocalDateTime createdAt;
}

