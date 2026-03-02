package com.floodrescue.backend.admin.service;

import com.floodrescue.backend.admin.dto.*;
import com.floodrescue.backend.manager.model.Item;
import com.floodrescue.backend.manager.model.VehicleType;
import com.floodrescue.backend.manager.repository.ItemRepository;
import com.floodrescue.backend.manager.repository.VehicleTypeRepository;
import com.floodrescue.backend.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * AD-02: Cấu hình danh mục - Item (loại nhu yếu phẩm), VehicleType (loại phương tiện).
 * Chỉ Admin được chỉnh sửa. Áp dụng cho ca mới.
 */
@Service
@RequiredArgsConstructor
public class CatalogService {

    private final ItemRepository itemRepository;
    private final VehicleTypeRepository vehicleTypeRepository;

    // --- Item Catalog ---
    public List<ItemCatalogResponse> getAllItems() {
        return itemRepository.findAll().stream()
                .map(ItemCatalogResponse::from)
                .toList();
    }

    public List<ItemCatalogResponse> getActiveItems() {
        return itemRepository.findByStatus("ACTIVE").stream()
                .map(ItemCatalogResponse::from)
                .toList();
    }

    public ItemCatalogResponse getItemById(Integer id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with id: " + id));
        return ItemCatalogResponse.from(item);
    }

    public ItemCatalogResponse createItem(ItemCatalogRequest request) {
        Item item = new Item();
        item.setName(request.getName());
        item.setItemType(request.getItemType());
        item.setCapacity(request.getCapacity());
        item.setStatus(request.getStatus() != null ? request.getStatus() : "ACTIVE");
        item = itemRepository.save(item);
        return ItemCatalogResponse.from(item);
    }

    public ItemCatalogResponse updateItem(Integer id, ItemCatalogRequest request) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with id: " + id));
        item.setName(request.getName());
        item.setItemType(request.getItemType());
        item.setCapacity(request.getCapacity());
        if (request.getStatus() != null) {
            item.setStatus(request.getStatus());
        }
        item = itemRepository.save(item);
        return ItemCatalogResponse.from(item);
    }

    public void deleteItem(Integer id) {
        if (!itemRepository.existsById(id)) {
            throw new ResourceNotFoundException("Item not found with id: " + id);
        }
        itemRepository.deleteById(id);
    }

    // --- VehicleType Catalog ---
    public List<VehicleTypeCatalogResponse> getAllVehicleTypes() {
        return vehicleTypeRepository.findAll().stream()
                .map(VehicleTypeCatalogResponse::from)
                .toList();
    }

    public List<VehicleTypeCatalogResponse> getActiveVehicleTypes() {
        return vehicleTypeRepository.findByStatus("ACTIVE").stream()
                .map(VehicleTypeCatalogResponse::from)
                .toList();
    }

    public VehicleTypeCatalogResponse getVehicleTypeById(Integer id) {
        VehicleType vehicleType = vehicleTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("VehicleType not found with id: " + id));
        return VehicleTypeCatalogResponse.from(vehicleType);
    }

    public VehicleTypeCatalogResponse createVehicleType(VehicleTypeCatalogRequest request) {
        VehicleType vehicleType = new VehicleType();
        vehicleType.setName(request.getName());
        vehicleType.setStatus(request.getStatus() != null ? request.getStatus() : "ACTIVE");
        vehicleType = vehicleTypeRepository.save(vehicleType);
        return VehicleTypeCatalogResponse.from(vehicleType);
    }

    public VehicleTypeCatalogResponse updateVehicleType(Integer id, VehicleTypeCatalogRequest request) {
        VehicleType vehicleType = vehicleTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("VehicleType not found with id: " + id));
        vehicleType.setName(request.getName());
        if (request.getStatus() != null) {
            vehicleType.setStatus(request.getStatus());
        }
        vehicleType = vehicleTypeRepository.save(vehicleType);
        return VehicleTypeCatalogResponse.from(vehicleType);
    }

    public void deleteVehicleType(Integer id) {
        if (!vehicleTypeRepository.existsById(id)) {
            throw new ResourceNotFoundException("VehicleType not found with id: " + id);
        }
        vehicleTypeRepository.deleteById(id);
    }
}
