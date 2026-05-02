package com.krishanu.inventory.inventory_service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.krishanu.inventory.common.event.StockEvent;
import com.krishanu.inventory.common.event.StockTypeEnum;
import com.krishanu.inventory.inventory_service.event.consumer.StockEventConsumer;
import com.krishanu.inventory.inventory_service.repository.ProcessedEventRepository;
import com.krishanu.inventory.inventory_service.service.InventoryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockEventConsumerTest {

    @Mock
    private InventoryService inventoryService;

    @Mock
    private ProcessedEventRepository processedEventRepository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private StockEventConsumer consumer;

    @Test
    void shouldIgnoreDuplicateEvent() throws Exception {

        String eventId = UUID.randomUUID().toString();
        StockEvent event = new StockEvent(
                UUID.randomUUID(),
                eventId,
                10,
                StockTypeEnum.SALE,
                Instant.now()
        );


        when(objectMapper.readValue(anyString(), eq(StockEvent.class)))
                .thenReturn(event);

        when(processedEventRepository.existsById(eventId))
                .thenReturn(true);

        consumer.consume("{json}");

        verify(inventoryService, never())
                .increaseStock(any(), anyInt());
    }
}