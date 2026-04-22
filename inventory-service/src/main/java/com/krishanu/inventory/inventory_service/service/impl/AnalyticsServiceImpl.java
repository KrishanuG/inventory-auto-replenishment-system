package com.krishanu.inventory.inventory_service.service.impl;

import com.krishanu.inventory.inventory_service.repository.EventLogRepository;
import com.krishanu.inventory.inventory_service.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AnalyticsServiceImpl implements AnalyticsService {

    private final EventLogRepository repository;

    @Override
    public List<Map<String, Object>> getFastMovingProducts() {

        return repository.findTopSellingProducts()
                .stream()
                .map(row -> Map.of(
                        "productId", row[0],
                        "totalSold", row[1]
                ))
                .toList();
    }

    @Override
    public List<Map<String, Object>> getStockOutTrends() {

        return repository.findStockOutTrends()
                .stream()
                .map(row -> Map.of(
                        "productId", row[0],
                        "stockOutCount", row[1]
                ))
                .toList();
    }
}
