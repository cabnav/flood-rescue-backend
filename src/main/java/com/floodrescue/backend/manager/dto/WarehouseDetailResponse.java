package com.floodrescue.backend.manager.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseDetailResponse {
    private Integer id;
    private Integer userId;
    private String resourceId;
    private String supplyId;
    private String status;
}
