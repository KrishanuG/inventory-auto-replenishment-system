package com.krishanu.inventory.inventory_service.service.impl;

import com.krishanu.inventory.inventory_service.dto.InventoryResponse;
import com.krishanu.inventory.inventory_service.entity.Inventory;
import com.krishanu.inventory.inventory_service.entity.Product;
import com.krishanu.inventory.inventory_service.exception.InsufficientStockException;
import com.krishanu.inventory.inventory_service.repository.InventoryRepository;
import com.krishanu.inventory.inventory_service.repository.ProductRepository;
import com.krishanu.inventory.inventory_service.service.InventoryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
// Entity is managed; changes will be flushed automatically due to @Transactional
public class InventoryServiceImpl implements InventoryService {
    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;

    @Override
    public InventoryResponse increaseStock(UUID productId, Integer quantity) {
        log.info("Increasing stock for productId={} by quantity={}", productId, quantity);

        Inventory inventory = getInventoryByProductId(productId);

        // Hibernate dirty checking will persist this change on transaction commit
        inventory.setQuantity(inventory.getQuantity() + quantity);
        log.info("New stock level for productId={} is {}", productId, inventory.getQuantity());

        return buildInventoryResponse(inventory);
    }

    @Override
    public InventoryResponse decreaseStock(UUID productId, Integer quantity) {
        log.info("Decreasing stock for productId={} by quantity={}", productId, quantity);
        Inventory inventory = getInventoryByProductId(productId);

        if (inventory.getQuantity() < quantity) {
            log.warn("Stock underflow attempt for productId={} | current={} | requested={}",
                    productId, inventory.getQuantity(), quantity);
            throw new InsufficientStockException("Quantity should be greater than stock quantity");
        }
        // Hibernate dirty checking will persist this change on transaction commit, no need to save explicitly
        inventory.setQuantity(inventory.getQuantity() - quantity);
        log.info("stock updated - new stock for productId={} is {}", productId, inventory.getQuantity());

        return buildInventoryResponse(inventory);
    }

    private Inventory getInventoryByProductId(UUID productId) {

        Product product = productRepository.findById(productId).
                orElseThrow(() -> new ResourceNotFoundException("Product not found with id " + productId)
                );

        return inventoryRepository.findByProduct(product).
                orElseThrow(() -> new ResourceNotFoundException("Inventory not found with id " + productId));
    }

    private InventoryResponse buildInventoryResponse(Inventory inventory) {
        boolean lowStock = inventory.getQuantity() < inventory.getProduct().getMinStockThreshold();
        return InventoryResponse.builder()
                .productId(inventory.getProduct().getId())
                .quantity(inventory.getQuantity())
                .reservedQuantity(inventory.getReservedQuantity())
                .lowStock(lowStock)
                .build();
    }
}
