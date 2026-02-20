package com.krishanu.inventory.inventory_service.event.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.krishanu.inventory.inventory_service.event.StockEvent;
import com.krishanu.inventory.inventory_service.service.InventoryService;
import com.krishanu.inventory.inventory_service.utils.StockTypeEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StockEventConsumer {
    private final ObjectMapper objectMapper;
    private final InventoryService inventoryService;

    @KafkaListener(topics = "stock-event", groupId = "inventory-group")
    private void consume(String message) {
        try {
            StockEvent event = objectMapper.readValue(message, StockEvent.class);
            log.info("Received stock event from kafka : type={} , productId={}", event.getType(), event.getProductId());

            if (event.getType().equals(StockTypeEnum.LOW_STOCK)) {
                log.warn("Auto-replenishment triggered for product id={}", event.getProductId());
                //simulate replenishment
                inventoryService.increaseStock(event.getProductId(), 20); //fixed quantity for just simulation
            }
            log.info("auto-replenishment completed for product id={}", event.getProductId());
        } catch (Exception e) {
            log.error("Failed to process kafka message : {}", e.getMessage());
        }
    }
}
