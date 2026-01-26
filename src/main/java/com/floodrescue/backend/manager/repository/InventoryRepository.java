package com.floodrescue.backend.manager.repository;

import com.floodrescue.backend.manager.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Integer> {
    List<Inventory> findByWarehouseId(Integer warehouseId);
    List<Inventory> findByItemId(Integer itemId);
}
