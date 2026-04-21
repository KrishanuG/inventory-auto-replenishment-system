package com.krishanu.procurement.controller;

import com.krishanu.procurement.dto.PurchaseOrderResponse;
import com.krishanu.procurement.service.PurchaseOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/purchase-orders")
@RequiredArgsConstructor
public class PurchaseOrderController {

    private final PurchaseOrderService service;

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping
    public ResponseEntity<List<PurchaseOrderResponse>> getAllPurchaseOrders() {
        return ResponseEntity.ok(service.getAllPurchaseOrders());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/approve")
    public ResponseEntity<?> approve(@PathVariable UUID id) {
        return ResponseEntity.ok(service.approve(id));
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/receive")
    public ResponseEntity<?> receive(@PathVariable UUID id) {
        return ResponseEntity.ok(service.receive(id));
    }
}
