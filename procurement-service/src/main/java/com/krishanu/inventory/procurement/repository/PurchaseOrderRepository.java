package com.krishanu.inventory.procurement.repository;

import com.krishanu.inventory.procurement.entity.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PurchaseOrderRepository
        extends JpaRepository<PurchaseOrder, UUID> {
}
