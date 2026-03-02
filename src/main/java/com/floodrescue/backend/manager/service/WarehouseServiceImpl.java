package com.floodrescue.backend.manager.service;

import com.floodrescue.backend.auth.model.User;
import com.floodrescue.backend.auth.repository.UserRepository;
import com.floodrescue.backend.common.exception.BadRequestException;
import com.floodrescue.backend.manager.dto.InventoryMovementRequest;
import com.floodrescue.backend.manager.dto.WarehouseDetailResponse;
import com.floodrescue.backend.manager.dto.WarehouseInventoryResponse;
import com.floodrescue.backend.manager.model.Inventory;
import com.floodrescue.backend.manager.model.InventoryTransaction;
import com.floodrescue.backend.manager.model.Item;
import com.floodrescue.backend.manager.model.Warehouse;
import com.floodrescue.backend.manager.repository.InventoryRepository;
import com.floodrescue.backend.manager.repository.InventoryTransactionRepository;
import com.floodrescue.backend.manager.repository.ItemRepository;
import com.floodrescue.backend.manager.repository.WarehouseRepository;
import com.floodrescue.backend.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WarehouseServiceImpl implements WarehouseService {

    private final WarehouseRepository warehouseRepository;
    private final InventoryRepository inventoryRepository;
    private final InventoryTransactionRepository inventoryTransactionRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public WarehouseDetailResponse createWarehouse(com.floodrescue.backend.manager.dto.CreateWarehouseRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getUserId()));

        Warehouse warehouse = new Warehouse();
        warehouse.setUser(user);
        warehouse.setResourceId(request.getResourceId());
        warehouse.setSupplyId(request.getSupplyId());
        warehouse.setStatus(request.getStatus());

        Warehouse saved = warehouseRepository.save(warehouse);
        return mapToDetailResponse(saved);
    }

    @Override
    public WarehouseDetailResponse getWarehouseById(Integer id) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found with id: " + id));
        return mapToDetailResponse(warehouse);
    }

    @Override
    public List<WarehouseDetailResponse> getAllWarehouses() {
        return warehouseRepository.findAll()
                .stream()
                .map(this::mapToDetailResponse)
                .collect(Collectors.toList());
    }

    @Override
    public WarehouseInventoryResponse getWarehouseInventory(Integer warehouseId) {
        Warehouse warehouse = warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found with id: " + warehouseId));

        List<Inventory> inventories = inventoryRepository.findByWarehouseId(warehouse.getId());

        WarehouseInventoryResponse response = new WarehouseInventoryResponse();
        response.setWarehouseId(warehouse.getId());
        response.setItems(
                inventories.stream()
                        .map(inv -> new WarehouseInventoryResponse.InventoryItem(
                                inv.getItem().getId(),
                                inv.getItem().getName(),
                                inv.getQuantity()
                        ))
                        .collect(Collectors.toList())
        );

        return response;
    }

    @Override
    public WarehouseInventoryResponse importInventory(Integer warehouseId, InventoryMovementRequest request) {
        Warehouse warehouse = warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found with id: " + warehouseId));
        Item item = itemRepository.findById(request.getItemId())
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with id: " + request.getItemId()));

        if (request.getQuantity() == null || request.getQuantity() <= 0) {
            throw new BadRequestException("Số lượng nhập phải lớn hơn 0");
        }

        Inventory inventory = inventoryRepository.findByWarehouseIdAndItemId(warehouse.getId(), item.getId());
        if (inventory == null) {
            inventory = new Inventory();
            inventory.setWarehouse(warehouse);
            inventory.setItem(item);
            inventory.setQuantity(0);
        }

        int before = inventory.getQuantity();
        int after = before + request.getQuantity();
        inventory.setQuantity(after);
        inventoryRepository.save(inventory);

        InventoryTransaction transaction = new InventoryTransaction();
        transaction.setInventory(inventory);
        transaction.setTransactionType(InventoryTransaction.TransactionType.IN);
        transaction.setQuantity(request.getQuantity());
        transaction.setBeforeQuantity(before);
        transaction.setAfterQuantity(after);
        transaction.setUser(null);
        inventoryTransactionRepository.save(transaction);

        return getWarehouseInventory(warehouseId);
    }

    @Override
    public WarehouseInventoryResponse exportInventory(Integer warehouseId, InventoryMovementRequest request) {
        Warehouse warehouse = warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found with id: " + warehouseId));
        Item item = itemRepository.findById(request.getItemId())
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with id: " + request.getItemId()));

        if (request.getQuantity() == null || request.getQuantity() <= 0) {
            throw new BadRequestException("Số lượng xuất phải lớn hơn 0");
        }

        Inventory inventory = inventoryRepository.findByWarehouseIdAndItemId(warehouse.getId(), item.getId());
        if (inventory == null || inventory.getQuantity() < request.getQuantity()) {
            throw new BadRequestException("Không đủ tồn kho để xuất");
        }

        int before = inventory.getQuantity();
        int after = before - request.getQuantity();
        inventory.setQuantity(after);
        inventoryRepository.save(inventory);

        InventoryTransaction transaction = new InventoryTransaction();
        transaction.setInventory(inventory);
        transaction.setTransactionType(InventoryTransaction.TransactionType.OUT);
        transaction.setQuantity(request.getQuantity());
        transaction.setBeforeQuantity(before);
        transaction.setAfterQuantity(after);
        transaction.setUser(null);
        inventoryTransactionRepository.save(transaction);

        return getWarehouseInventory(warehouseId);
    }

    private WarehouseDetailResponse mapToDetailResponse(Warehouse warehouse) {
        Integer userId = warehouse.getUser() != null ? warehouse.getUser().getId() : null;
        WarehouseDetailResponse response = new WarehouseDetailResponse();
        response.setId(warehouse.getId());
        response.setUserId(userId);
        response.setResourceId(warehouse.getResourceId());
        response.setSupplyId(warehouse.getSupplyId());
        response.setStatus(warehouse.getStatus());
        return response;
    }
}
