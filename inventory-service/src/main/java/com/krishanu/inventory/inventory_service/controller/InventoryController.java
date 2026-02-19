package com.krishanu.inventory.inventory_service.controller;

import com.krishanu.inventory.inventory_service.dto.InventoryResponse;
import com.krishanu.inventory.inventory_service.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {
    private final InventoryService inventoryService;

    @PostMapping("/{productId}/stock/increase")
    public ResponseEntity<InventoryResponse> increaseStock(
            @PathVariable UUID productId,
            @RequestParam int quantity
    ) {
        return ResponseEntity.ok(inventoryService.increaseStock(productId, quantity));
    }

    @PostMapping("/{productId}/stock/decrease")
    public ResponseEntity<InventoryResponse> decreaseStock(
            @PathVariable UUID productId,
            @RequestParam int quantity
    ) {
        return ResponseEntity.ok(inventoryService.decreaseStock(productId, quantity));
    }
}
