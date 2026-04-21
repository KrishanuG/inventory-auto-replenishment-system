package com.krishanu.inventory.entity;

import com.krishanu.inventory.utils.PurchaseOrderStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "purchase_orders")
public class PurchaseOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private UUID productId;

    private Integer quantity;

    @Enumerated(EnumType.STRING)
    private PurchaseOrderStatus status;

    private Instant createdAt;

    private Instant updatedAt;
}
