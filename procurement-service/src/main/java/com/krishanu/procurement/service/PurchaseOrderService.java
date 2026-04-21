package com.krishanu.procurement.service;

import com.krishanu.procurement.dto.PurchaseOrderResponse;
import com.krishanu.procurement.entity.PurchaseOrder;

import java.util.List;
import java.util.UUID;

public interface PurchaseOrderService {
    PurchaseOrder approve(UUID id);

    PurchaseOrder receive(UUID id);

    List<PurchaseOrderResponse> getAllPurchaseOrders();
}
