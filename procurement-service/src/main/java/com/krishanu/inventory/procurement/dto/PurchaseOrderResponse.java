package com.krishanu.inventory.procurement.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Builder
public class PurchaseOrderResponse {

    private UUID id;
    private UUID productId;
    private int quantity;
    private String status;
    private Instant createdAt;
}
