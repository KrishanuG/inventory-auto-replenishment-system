package com.krishanu.inventory.inventory_service.service.impl;

import com.krishanu.inventory.inventory_service.dto.InventoryResponse;
import com.krishanu.inventory.inventory_service.entity.Inventory;
import com.krishanu.inventory.inventory_service.entity.Product;
import com.krishanu.inventory.inventory_service.event.StockEvent;
import com.krishanu.inventory.inventory_service.event.producer.StockEventProducer;
import com.krishanu.inventory.inventory_service.exception.InsufficientStockException;
import com.krishanu.inventory.inventory_service.repository.InventoryRepository;
import com.krishanu.inventory.inventory_service.repository.ProductRepository;
import com.krishanu.inventory.inventory_service.service.InventoryService;
import com.krishanu.inventory.inventory_service.utils.StockTypeEnum;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
// Entity is managed; changes will be flushed automatically due to @Transactional
public class InventoryServiceImpl implements InventoryService {
    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;
    private final StockEventProducer stockEventProducer;

    @Override
    public InventoryResponse increaseStock(UUID productId, Integer quantity) {
        log.info("Increasing stock for productId={} by quantity={}", productId, quantity);

        Inventory inventory = getInventoryByProductId(productId);

        // Hibernate dirty checking will persist this change on transaction commit
        inventory.setQuantity(inventory.getQuantity() + quantity);
        log.info("New stock level for productId={} is {}", productId, inventory.getQuantity());

        return buildInventoryResponse(inventory, false);
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

        //publish sale event
        StockEvent stockEvent = StockEvent.builder()
                .productId(productId)
                .type(StockTypeEnum.SALE)
                .quantity(quantity)
                .timestamp(Instant.now())
                .build();

        stockEventProducer.publish(stockEvent);

        boolean lowStock = inventory.getQuantity() < inventory.getProduct().getMinStockThreshold();

        //publish low stock event
        if (lowStock) {
            log.warn("Low stock detected for productId={} | currentQuantity={}",
                    productId, inventory.getQuantity());
            StockEvent lowStockEvent = StockEvent.builder()
                    .productId(productId)
                    .type(StockTypeEnum.LOW_STOCK)
                    .quantity(inventory.getQuantity())
                    .timestamp(Instant.now())
                    .build();

            stockEventProducer.publish(lowStockEvent);
        }
        return buildInventoryResponse(inventory, lowStock);
    }

    public Inventory getInventoryByProductId(UUID productId) {

        Product product = productRepository.findById(productId).
                orElseThrow(() -> new ResourceNotFoundException("Product not found with id " + productId)
                );

        return inventoryRepository.findByProduct(product).
                orElseThrow(() -> new ResourceNotFoundException("Inventory not found with id " + productId));
    }

    private InventoryResponse buildInventoryResponse(Inventory inventory, boolean lowStockStatus) {
        return InventoryResponse.builder()
                .productId(inventory.getProduct().getId())
                .quantity(inventory.getQuantity())
                .reservedQuantity(inventory.getReservedQuantity())
                .lowStock(lowStockStatus)
                .build();
    }
}
