package com.krishanu.inventory.inventory_service.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class InventoryResponse {
    private UUID productId;
    private Integer quantity;
    private Integer reservedQuantity;
    private boolean lowStock;
}
