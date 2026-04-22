package com.krishanu.inventory.inventory_service.controller;

import com.krishanu.inventory.inventory_service.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/fast-moving")
    public ResponseEntity<?> fastMoving() {
        return ResponseEntity.ok(analyticsService.getFastMovingProducts());
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/stock-outs")
    public ResponseEntity<?> stockOuts() {
        return ResponseEntity.ok(analyticsService.getStockOutTrends());
    }
}
