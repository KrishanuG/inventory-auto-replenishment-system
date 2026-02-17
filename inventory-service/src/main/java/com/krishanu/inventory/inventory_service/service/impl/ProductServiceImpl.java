package com.krishanu.inventory.inventory_service.service.impl;

import com.krishanu.inventory.inventory_service.dto.PagedResponse;
import com.krishanu.inventory.inventory_service.dto.ProductRequest;
import com.krishanu.inventory.inventory_service.dto.ProductResponse;
import com.krishanu.inventory.inventory_service.entity.Product;
import com.krishanu.inventory.inventory_service.exception.DuplicateResourceException;
import com.krishanu.inventory.inventory_service.exception.ProductNotFoundException;
import com.krishanu.inventory.inventory_service.mapper.ProductMapper;
import com.krishanu.inventory.inventory_service.repository.ProductRepository;
import com.krishanu.inventory.inventory_service.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;

    @Override
    public ProductResponse createProduct(ProductRequest productRequest) {

        if (productRepository.existsBySku(productRequest.getSku())) {
            throw new DuplicateResourceException("SKU Already Exists");
        }

        Product product = productRepository.save(ProductMapper.toEntity(productRequest));

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
}
