package com.floodrescue.backend.manager.repository;

import com.floodrescue.backend.manager.model.InventoryTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryTransactionRepository extends JpaRepository<InventoryTransaction, Integer> {

    List<InventoryTransaction> findByInventoryIdOrderByCreatedAtDesc(Integer inventoryId);

    List<InventoryTransaction> findByInventory_Warehouse_IdOrderByCreatedAtDesc(Integer warehouseId);
}

