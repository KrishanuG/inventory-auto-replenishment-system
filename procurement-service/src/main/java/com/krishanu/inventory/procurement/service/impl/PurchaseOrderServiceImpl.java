package com.krishanu.inventory.procurement.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.krishanu.inventory.procurement.dto.PurchaseOrderResponse;
import com.krishanu.inventory.procurement.entity.PurchaseOrder;
import com.krishanu.inventory.procurement.mapper.PurchaseOrderMapper;
import com.krishanu.inventory.procurement.repository.PurchaseOrderRepository;
import com.krishanu.inventory.procurement.service.PurchaseOrderService;
import com.krishanu.inventory.procurement.utils.PurchaseOrderStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PurchaseOrderServiceImpl implements PurchaseOrderService {

    private final PurchaseOrderRepository repository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${app.kafka.topics.receive-goods}")
    private String receiveGoodsTopic;

    @Override
    @Transactional
    public PurchaseOrder approve(UUID id) {

        PurchaseOrder po = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("PO not found"));

        if (po.getStatus() != PurchaseOrderStatus.CREATED) {
            throw new IllegalStateException("Only CREATED PO can be approved");
        }

        po.setStatus(PurchaseOrderStatus.APPROVED);
        po.setUpdatedAt(Instant.now());

        log.info("PO approved id={}", id);

        return repository.save(po);
    }

    @Override
    @Transactional
    public PurchaseOrder receive(UUID id) {

        PurchaseOrder po = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("PO not found"));

        if (po.getStatus() != PurchaseOrderStatus.APPROVED) {
            throw new IllegalStateException("Only APPROVED PO can be received");
        }

        po.setStatus(PurchaseOrderStatus.RECEIVED);
        po.setUpdatedAt(Instant.now());

        // Publish RECEIVE_GOODS event
        publishReceiveGoodsEvent(po);

        log.info("PO received id={}", id);

        return repository.save(po);
    }

    private void publishReceiveGoodsEvent(PurchaseOrder po) {

        try {
            Map<String, Object> event = Map.of(
                    "productId", po.getProductId(),
                    "quantity", po.getQuantity()
            );

            String payload = objectMapper.writeValueAsString(event);

            kafkaTemplate.send(receiveGoodsTopic, po.getProductId().toString(), payload);

            log.info("Published RECEIVE_GOODS event for productId={}", po.getProductId());

        } catch (Exception e) {
            throw new RuntimeException("Failed to publish RECEIVE_GOODS", e);
        }
    }

    @Override
    public List<PurchaseOrderResponse> getAllPurchaseOrders() {

        return repository.findAll(Sort.by(Sort.Direction.DESC,"createdAt"))
                .stream()
                .map(PurchaseOrderMapper::toResponse)
                .toList();
    }
}
