package com.floodrescue.backend.manager.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseInventoryResponse {
    private Integer warehouseId;
    private List<InventoryItem> items;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InventoryItem {
        private Integer itemId;
        private String itemName;
        private Integer quantity;
    }
}
