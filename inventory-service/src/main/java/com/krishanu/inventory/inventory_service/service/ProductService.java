package com.krishanu.inventory.inventory_service.service;

import com.krishanu.inventory.inventory_service.dto.PagedResponse;
import com.krishanu.inventory.inventory_service.dto.ProductRequest;
import com.krishanu.inventory.inventory_service.dto.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ProductService {

    ProductResponse createProduct(ProductRequest productRequest);

    ProductResponse getProduct(UUID id);

    PagedResponse<ProductResponse> getAllProducts(Pageable pageable);

}
