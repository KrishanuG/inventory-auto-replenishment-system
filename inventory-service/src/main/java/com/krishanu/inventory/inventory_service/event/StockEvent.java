package com.krishanu.inventory.inventory_service.event;

import com.krishanu.inventory.inventory_service.utils.StockTypeEnum;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Builder
public class StockEvent {
    private UUID productId;
    private int quantity;
    private StockTypeEnum type; // SALE or LOW_STOCK
    private Instant timestamp;
}
