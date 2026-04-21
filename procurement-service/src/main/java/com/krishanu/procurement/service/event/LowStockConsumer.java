package com.krishanu.procurement.service.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.krishanu.procurement.entity.PurchaseOrder;
import com.krishanu.procurement.repository.PurchaseOrderRepository;
import com.krishanu.procurement.utils.PurchaseOrderStatus;
import com.krishanu.procurement.utils.StockTypeEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class LowStockConsumer {

    private final ObjectMapper objectMapper;
    private final PurchaseOrderRepository purchaseOrderRepository;

    @KafkaListener(
            topics = "${app.kafka.topics.stock-event}",
            groupId = "${app.kafka.group-id.procurement}"
    )
    @Transactional
    public void consume(String message) throws Exception {

        StockEvent event = objectMapper.readValue(message, StockEvent.class);

        if (event.getType() == StockTypeEnum.LOW_STOCK) {

            log.warn("LOW_STOCK received for productId={}", event.getProductId());

            PurchaseOrder po = PurchaseOrder.builder()
                    .productId(event.getProductId())
                    .quantity(50) // initial simulation
                    .status(PurchaseOrderStatus.CREATED)
                    .createdAt(Instant.now())
                    .updatedAt(Instant.now())
                    .build();

            purchaseOrderRepository.save(po);

            log.info("PurchaseOrder created with id={}", po.getId());
        }
    }
}