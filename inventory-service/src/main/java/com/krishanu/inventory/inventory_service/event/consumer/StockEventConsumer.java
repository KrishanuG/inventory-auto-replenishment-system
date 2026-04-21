package com.krishanu.inventory.inventory_service.event.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.krishanu.inventory.inventory_service.event.StockEvent;
import com.krishanu.inventory.inventory_service.repository.ProcessedEventRepository;
import com.krishanu.inventory.inventory_service.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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
    public void consume(String message) throws Exception {

        StockEvent event = objectMapper.readValue(message, StockEvent.class);
        log.info("Received stock event from kafka : type={} , productId={}", event.getType(), event.getProductId());

        //idempotency check
        if (processedEventRepository.existsById(event.getEventId())) {
            log.warn("Event already processed: {}", event.getEventId());
        }

    }

    @KafkaListener(topics = "${app.kafka.topics.stock-event-dlt}", groupId = "${app.kafka.group-id.inventory}")
    public void handleDeadLetter(String message) {
        log.error("DLT Received Failed Message: {}", message);
    }
}
