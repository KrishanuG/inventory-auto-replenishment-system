package com.krishanu.inventory.inventory_service.service.impl;

import com.krishanu.inventory.inventory_service.dto.PagedResponse;
import com.krishanu.inventory.inventory_service.dto.ProductRequest;
import com.krishanu.inventory.inventory_service.dto.ProductResponse;
import com.krishanu.inventory.inventory_service.entity.Inventory;
import com.krishanu.inventory.inventory_service.entity.Product;
import com.krishanu.inventory.inventory_service.exception.DuplicateResourceException;
import com.krishanu.inventory.inventory_service.exception.ProductNotFoundException;
import com.krishanu.inventory.inventory_service.mapper.ProductMapper;
import com.krishanu.inventory.inventory_service.repository.InventoryRepository;
import com.krishanu.inventory.inventory_service.repository.ProductRepository;
import com.krishanu.inventory.inventory_service.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;

    @Override
    public ProductResponse createProduct(ProductRequest productRequest) {
        log.info("----------- createProduct - start ---------------");
        log.info("creating product with sku: {}", productRequest.getSku());

        if (productRepository.existsBySku(productRequest.getSku())) {
            throw new DuplicateResourceException("SKU Already Exists");
        }

        Product product = productRepository.save(ProductMapper.toEntity(productRequest));
        //when the product is created inventory record also needs to be created
        Inventory inventory = Inventory.builder()
                .product(product)
                .quantity(0) // initial value
                .reservedQuantity(0)
                .build();
        inventoryRepository.save(inventory);

        return ProductMapper.toResponse(product);
    }

    @Override
    public ProductResponse getProduct(UUID id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException("Product Not Found"));
        return ProductMapper.toResponse(product);
    }

    @Override
    public PagedResponse<ProductResponse> getAllProducts(Pageable pageable) {
        Page<Product> productPage = productRepository.findAll(pageable);

        List<ProductResponse> content = productPage.getContent().stream().map(ProductMapper::toResponse).toList();
        return PagedResponse.<ProductResponse>builder()
                .content(content)
                .pageNumber(productPage.getNumber())
                .pageSize(productPage.getSize())
                .totalPages(productPage.getTotalPages())
                .totalElements(productPage.getTotalElements())
                .last(productPage.isLast())
                .build();
    }

    @Override
    public PagedResponse<ProductResponse> getProductsByKeyset(Instant lastCreatedAt, UUID lastId, int size) {
        Pageable pageable = PageRequest.of(0, size);

        List<Product> products;

        if (lastCreatedAt == null || lastId == null) {
            products = productRepository.findFirstPage(pageable);
        } else {
            products = productRepository.findNextPage(lastCreatedAt, lastId, pageable);
        }

        List<ProductResponse> content = products.stream()
                .map(ProductMapper::toResponse)
                .toList();

        // We don’t compute full count — because keyset doesn’t rely on total count.
        return PagedResponse.<ProductResponse>builder()
                .content(content)
                .pageNumber(0)
                .pageSize(size)
                .totalElements(content.size())
                .totalPages(1)
                .last(content.size() < size)
                .build();
    }
}
