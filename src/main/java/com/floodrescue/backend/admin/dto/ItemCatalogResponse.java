package com.floodrescue.backend.admin.dto;

import com.floodrescue.backend.manager.model.Item;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemCatalogResponse {
    private Integer id;
    private String name;
    private Item.ItemType itemType;
    private String capacity;
    private String status;

    public static ItemCatalogResponse from(Item item) {
        return new ItemCatalogResponse(
                item.getId(),
                item.getName(),
                item.getItemType(),
                item.getCapacity(),
                item.getStatus()
        );
    }
}
