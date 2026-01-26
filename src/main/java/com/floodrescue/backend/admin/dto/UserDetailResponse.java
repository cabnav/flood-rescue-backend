package com.floodrescue.backend.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailResponse {
    private Integer id;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String role;
    private Boolean isActive;
    private LocalDateTime createdAt;
}
