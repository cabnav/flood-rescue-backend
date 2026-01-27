package com.floodrescue.backend.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {
    private Integer id;
    private String email;
    private String fullName;
    private String phoneNumber;
    private String role;
    private String avatar;
}
