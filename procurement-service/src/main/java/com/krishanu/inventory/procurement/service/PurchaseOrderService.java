package com.krishanu.inventory.procurement.service;

import com.krishanu.inventory.procurement.dto.PurchaseOrderResponse;
import com.krishanu.inventory.procurement.entity.PurchaseOrder;

import java.util.List;
import java.util.UUID;

public interface PurchaseOrderService {
    PurchaseOrder approve(UUID id);

    PurchaseOrder receive(UUID id);

    List<PurchaseOrderResponse> getAllPurchaseOrders();
}
