package com.floodrescue.backend.citizen.dto;

import com.floodrescue.backend.citizen.model.Request;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateRequestRequest {
    private Integer userId;
    private String phone;
    private Request.RequestType requestType;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String description;
    private Request.Priority priority;
    private String requestSupplies;
    private String requestMedia;
}
