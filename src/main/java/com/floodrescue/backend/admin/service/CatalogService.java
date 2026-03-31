package com.floodrescue.backend.admin.service;

import com.floodrescue.backend.admin.dto.*;

import java.util.List;

/**
 * AD-02: Cấu hình danh mục - Item (loại nhu yếu phẩm), VehicleType (loại phương tiện).
 * Chỉ Admin được chỉnh sửa. Áp dụng cho ca mới.
 */
public interface CatalogService {

    // --- Item Catalog ---
    List<ItemCatalogResponse> getAllItems();

    List<ItemCatalogResponse> getActiveItems();

    ItemCatalogResponse getItemById(Integer id);

    ItemCatalogResponse createItem(ItemCatalogRequest request);

    ItemCatalogResponse updateItem(Integer id, ItemCatalogRequest request);

    void deleteItem(Integer id);

    // --- VehicleType Catalog ---
    List<VehicleTypeCatalogResponse> getAllVehicleTypes();

    List<VehicleTypeCatalogResponse> getActiveVehicleTypes();

    VehicleTypeCatalogResponse getVehicleTypeById(Integer id);

    VehicleTypeCatalogResponse createVehicleType(VehicleTypeCatalogRequest request);

    VehicleTypeCatalogResponse updateVehicleType(Integer id, VehicleTypeCatalogRequest request);

    void deleteVehicleType(Integer id);
}
