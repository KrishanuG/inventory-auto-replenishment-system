package com.krishanu.inventory.inventory_service.service;

import java.util.List;
import java.util.Map;

public interface AnalyticsService {
    List<Map<String, Object>> getFastMovingProducts();

    List<Map<String, Object>> getStockOutTrends();
}
