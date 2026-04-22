package com.krishanu.inventory.inventory_service.event.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.krishanu.inventory.inventory_service.entity.EventLog;
import com.krishanu.inventory.common.event.StockEvent;
import com.krishanu.inventory.inventory_service.repository.EventLogRepository;
import com.krishanu.inventory.common.event.StockTypeEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class StockEventProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final EventLogRepository eventLogRepository;

    @Value("${app.kafka.topics.stock-event}")
    private String topic;

    public void publish(StockEvent stockEvent) {
        log.info("Publishing stock event to kafka : {}", stockEvent.getType());
        eventLogRepository.save(
                EventLog.builder()
                        .productId(stockEvent.getProductId())
                        .eventId(stockEvent.getEventId())
                        .eventType(stockEvent.getType())
                        .quantity(stockEvent.getQuantity())
                        .timestamp(Instant.now())
                        .build()
        );
        try {
            String payload = objectMapper.writeValueAsString(stockEvent);
            kafkaTemplate.send(topic, stockEvent.getProductId().toString(), payload)
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            log.info("Event sent successfully to topic={}, partition={}, offset={}",
                                    result.getRecordMetadata().topic(),
                                    result.getRecordMetadata().partition(),
                                    result.getRecordMetadata().offset());
                        } else {
                            log.error("Failed to publish stock event: {}", stockEvent, ex);
                        }
                    });
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize stock event", e);
        }

    }

    public void publishStock(UUID productId, int quantity, StockTypeEnum type) {

        StockEvent stockEvent = StockEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .productId(productId)
                .type(type)
                .quantity(quantity)
                .timestamp(Instant.now())
                .build();

        this.publish(stockEvent);
    }
}
