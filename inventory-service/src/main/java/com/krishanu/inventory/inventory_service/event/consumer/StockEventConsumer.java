package com.krishanu.inventory.inventory_service.event.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.krishanu.inventory.inventory_service.entity.Inventory;
import com.krishanu.inventory.inventory_service.entity.ProcessedEvent;
import com.krishanu.inventory.inventory_service.event.StockEvent;
import com.krishanu.inventory.inventory_service.repository.ProcessedEventRepository;
import com.krishanu.inventory.inventory_service.service.InventoryService;
import com.krishanu.inventory.inventory_service.utils.StockTypeEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class StockEventConsumer {
    private final ObjectMapper objectMapper;
    private final InventoryService inventoryService;
    private final ProcessedEventRepository processedEventRepository;

    @KafkaListener(
            topics = "${app.kafka.topics.stock-event}",
            groupId = "${app.kafka.group-id.inventory}"
    )
    @Transactional
    private void consume(String message) {
        try {
            StockEvent event = objectMapper.readValue(message, StockEvent.class);
            log.info("Received stock event from kafka : type={} , productId={}", event.getType(), event.getProductId());

            //idempotency check
            if (processedEventRepository.existsById(event.getEventId())) {
                log.warn("Event already processed: {}", event.getEventId());
                return;
            }


            if (event.getType().equals(StockTypeEnum.LOW_STOCK)) {
                log.warn("Auto-replenishment triggered for product id={}", event.getProductId());
                Inventory inventory = inventoryService.getInventoryByProductId(event.getProductId());

                int currentQty = inventory.getQuantity();
                int maxThreshold = inventory.getProduct().getMaxStockThreshold();

                int replenishAmount = maxThreshold - currentQty;

                if (replenishAmount > 0) {
                    inventoryService.increaseStock(event.getProductId(), replenishAmount);

                    log.info("Auto-replenishment completed for productId={} | replenished={}",
                            event.getProductId(), replenishAmount);
                }
            }
            log.info("auto-replenishment completed for product id={}", event.getProductId());

            // Mark event as processed
            processedEventRepository.save(
                    ProcessedEvent.builder()
                            .eventId(event.getEventId())
                            .processedAt(Instant.now())
                            .build());

        } catch (Exception e) {
            log.error("Failed to process kafka message : {}", e.getMessage());
        }
    }
}
