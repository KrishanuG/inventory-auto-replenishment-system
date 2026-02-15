package com.krishanu.inventory.inventory_service.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponse {

    private UUID id;
    private String name;
    private String description;
    private String sku;
    private BigDecimal price;
    private Integer minStockThreshold;
    private Integer maxStockThreshold;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
