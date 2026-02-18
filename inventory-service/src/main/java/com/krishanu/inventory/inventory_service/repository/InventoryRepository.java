package com.krishanu.inventory.inventory_service.repository;

import com.krishanu.inventory.inventory_service.entity.Inventory;
import com.krishanu.inventory.inventory_service.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface InventoryRepository extends JpaRepository<Inventory, UUID> {

    Optional<Inventory> findByProduct(Product product);
}
