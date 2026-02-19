package com.krishanu.inventory.inventory_service.service;

import com.krishanu.inventory.inventory_service.dto.InventoryResponse;

import java.util.UUID;

public interface InventoryService {
    InventoryResponse increaseStock(UUID productId, Integer quantity);

    InventoryResponse decreaseStock(UUID productId, Integer quantity);
}
