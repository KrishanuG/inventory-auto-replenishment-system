package com.krishanu.procurement.mapper;

import com.krishanu.procurement.dto.PurchaseOrderResponse;
import com.krishanu.procurement.entity.PurchaseOrder;

public class PurchaseOrderMapper {

    public static PurchaseOrderResponse toResponse(PurchaseOrder po) {
        return PurchaseOrderResponse.builder()
                .id(po.getId())
                .productId(po.getProductId())
                .quantity(po.getQuantity())
                .status(po.getStatus().name())
                .createdAt(po.getCreatedAt())
                .build();
    }
}
