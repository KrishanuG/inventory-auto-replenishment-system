package com.krishanu.inventory.common.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class StockEvent {
    private UUID productId;
    private String eventId; // to check idempotency
    private int quantity;
    private StockTypeEnum type; // SALE or LOW_STOCK or DAMAGE
    private Instant timestamp;
}
