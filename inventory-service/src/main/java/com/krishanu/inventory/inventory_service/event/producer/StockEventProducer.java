package com.krishanu.inventory.inventory_service.event.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.krishanu.inventory.inventory_service.event.StockEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StockEventProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    private static final String TOPIC = "stock-event";

    public void publish(StockEvent stockEvent) {
        log.info("Publishing stock event to kafka : {}", stockEvent.getType());
        try {
            String payload = objectMapper.writeValueAsString(stockEvent);
            kafkaTemplate.send(TOPIC, payload)
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
}
