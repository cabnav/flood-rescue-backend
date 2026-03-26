package com.floodrescue.backend.manager.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateWarehouseRequest {
    private Integer userId;
    private String resourceId;
    private String supplyId;
    private String status;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String address;
}
