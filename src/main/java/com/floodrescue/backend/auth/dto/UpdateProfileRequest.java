package com.floodrescue.backend.auth.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileRequest {

    @Size(max = 255, message = "Full name must be at most 255 characters")
    private String fullName;

    @Pattern(regexp = "^0\\d{9,10}$", message = "Phone number must start with 0 and be 10-11 digits")
    private String phoneNumber;
}
