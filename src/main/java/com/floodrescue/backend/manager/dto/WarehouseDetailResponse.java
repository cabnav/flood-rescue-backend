package com.floodrescue.backend.manager.dto;

import com.floodrescue.backend.manager.model.Warehouse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseDetailResponse {
    private Integer id;
    private Integer userId;
    private String resourceId;
    private String supplyId;
    private Warehouse.WarehouseStatus status;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String address;
}
