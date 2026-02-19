package com.krishanu.inventory.inventory_service.repository;

import com.krishanu.inventory.inventory_service.entity.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {

    Optional<Product> findBySku(String sku);

    boolean existsBySku(String sku);

    @Query("""
            SELECT p FROM Product p
            ORDER BY p.createdAt ASC, p.id ASC
            """)
    List<Product> findFirstPage(Pageable pageable);


    @Query("""
            SELECT p FROM Product p
            WHERE (p.createdAt > :lastCreatedAt OR 
                   (p.createdAt = :lastCreatedAt AND p.id > :lastId))
            ORDER BY p.createdAt ASC, p.id ASC
            """)
    List<Product> findNextPage(
            @Param("lastCreatedAt") Instant lastCreatedAt,
            @Param("lastId") UUID lastId,
            Pageable pageable
    );


}
