package com.floodrescue.backend.admin.dto;

import com.floodrescue.backend.manager.model.Item;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemCatalogRequest {
    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Item type is required")
    private Item.ItemType itemType;

    private String capacity;

    @NotBlank(message = "Status is required")
    private String status = "ACTIVE";
}
