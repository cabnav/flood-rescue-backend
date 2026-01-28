package com.floodrescue.backend.citizen.dto;

import com.floodrescue.backend.citizen.model.Request;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateRequestRequest {

    @NotNull(message = "User ID is required")
    private Integer userId;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^0\\d{9,10}$", message = "Phone number must start with 0 and be 10-11 digits")
    private String phone;

    @NotNull(message = "Request type is required")
    private Request.RequestType requestType;

    @NotNull(message = "Latitude is required")
    private BigDecimal latitude;

    @NotNull(message = "Longitude is required")
    private BigDecimal longitude;

    private String description;

    @NotNull(message = "Priority is required")
    private Request.Priority priority;

    private String requestSupplies;
    private String requestMedia;
}
