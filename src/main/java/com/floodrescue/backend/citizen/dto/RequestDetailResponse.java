package com.floodrescue.backend.citizen.dto;

import com.floodrescue.backend.citizen.model.Request;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestDetailResponse {
    private Integer id;
    private Integer userId;
    private String phone;
    private Request.RequestType requestType;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String description;
    private Request.Priority priority;
    private Request.RequestStatus status;
    private String requestSupplies;
    private LocalDateTime createdAt;
}
