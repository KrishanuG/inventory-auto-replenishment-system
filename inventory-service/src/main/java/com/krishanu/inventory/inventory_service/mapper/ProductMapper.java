package com.krishanu.inventory.inventory_service.mapper;

import com.krishanu.inventory.inventory_service.dto.ProductRequest;
import com.krishanu.inventory.inventory_service.dto.ProductResponse;
import com.krishanu.inventory.inventory_service.entity.Product;

public class ProductMapper {
    private ProductMapper(){

    }
    public static Product toEntity(ProductRequest request){
        return Product.builder()
                .name(request.getName())
                .sku(request.getSku())
                .description(request.getDescription())
                .price(request.getPrice())
                .minStockThreshold(request.getMinStockThreshold())
                .maxStockThreshold(request.getMaxStockThreshold())
                .build();
    }

    public static ProductResponse toResponse(Product entity){
        return ProductResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .sku(entity.getSku())
                .price(entity.getPrice())
                .minStockThreshold(entity.getMinStockThreshold())
                .maxStockThreshold(entity.getMaxStockThreshold())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
