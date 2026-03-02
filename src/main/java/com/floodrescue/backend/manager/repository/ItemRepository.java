package com.floodrescue.backend.manager.repository;

import com.floodrescue.backend.manager.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Integer> {
    List<Item> findByStatus(String status);

    List<Item> findByItemType(Item.ItemType itemType);
}
