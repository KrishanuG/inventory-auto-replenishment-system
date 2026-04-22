package com.krishanu.inventory.inventory_service.entity;

import com.krishanu.inventory.inventory_service.utils.StockTypeEnum;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "event_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String eventId;

    private UUID productId;

    @Enumerated(EnumType.STRING)
    private StockTypeEnum eventType;

    private Integer quantity;

    private Instant timestamp;
}
