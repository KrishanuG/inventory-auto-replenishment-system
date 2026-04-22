package com.krishanu.inventory.inventory_service.repository;

import com.krishanu.inventory.inventory_service.entity.EventLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface EventLogRepository extends JpaRepository<EventLog, UUID> {

    // Fast-moving (SALE count)
    @Query("""
                SELECT e.productId, SUM(e.quantity)
                FROM EventLog e
                WHERE e.eventType = 'SALE'
                GROUP BY e.productId
                ORDER BY SUM(e.quantity) DESC
            """)
    List<Object[]> findTopSellingProducts();

    // Stock-out trends
    @Query("""
                SELECT e.productId, COUNT(e)
                FROM EventLog e
                WHERE e.eventType = 'LOW_STOCK'
                GROUP BY e.productId
                ORDER BY COUNT(e) DESC
            """)
    List<Object[]> findStockOutTrends();
}
