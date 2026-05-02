package com.krishanu.inventory.inventory_service;

import com.krishanu.inventory.common.event.StockTypeEnum;
import com.krishanu.inventory.inventory_service.entity.Inventory;
import com.krishanu.inventory.inventory_service.entity.Product;
import com.krishanu.inventory.inventory_service.event.producer.StockEventProducer;
import com.krishanu.inventory.inventory_service.exception.InsufficientStockException;
import com.krishanu.inventory.inventory_service.repository.InventoryRepository;
import com.krishanu.inventory.inventory_service.repository.ProductRepository;
import com.krishanu.inventory.inventory_service.service.impl.InventoryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InventoryServiceImplTest {

    @Mock
    private InventoryRepository inventoryRepository;
    @Mock
    private ProductRepository productRepository;

    @Mock
    private StockEventProducer stockEventProducer;

    @InjectMocks
    private InventoryServiceImpl inventoryService;

    private Inventory inventory;
    private UUID productId;

    @BeforeEach
    void setup() {
        productId = UUID.randomUUID();

        Product product = Product.builder()
                .id(productId)
                .minStockThreshold(5)
                .maxStockThreshold(50)
                .build();

        inventory = Inventory.builder()
                .product(product)
                .quantity(10)
                .build();

        when(productRepository.findById(productId)).thenReturn(Optional.ofNullable(product));
        when(inventoryRepository.findByProduct(product)).thenReturn(Optional.of(inventory));

    }

    @Test
    void shouldDecreaseStockSuccessfully() {

        inventoryService.decreaseStock(productId, 3);

        assertEquals(7, inventory.getQuantity());

        verify(stockEventProducer).publishStock(
                eq(productId),
                eq(3),
                eq(StockTypeEnum.SALE)
        );
    }

    @Test
    void shouldTriggerLowStockEventWhenBelowThreshold() {
        inventoryService.decreaseStock(productId, 6);

        assertEquals(4, inventory.getQuantity());

        verify(stockEventProducer).publishStock(
                eq(productId),
                eq(6),
                eq(StockTypeEnum.SALE)
        );

        verify(stockEventProducer).publishStock(
                eq(productId),
                eq(4),
                eq(StockTypeEnum.LOW_STOCK)
        );
    }

    @Test
    void shouldThrowExceptionWhenStockInsufficient() {
        assertThrows(InsufficientStockException.class,
                () -> inventoryService.decreaseStock(productId, 20));
    }
}
