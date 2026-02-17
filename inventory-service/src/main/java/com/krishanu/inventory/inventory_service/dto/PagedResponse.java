package com.krishanu.inventory.inventory_service.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PagedResponse<T> {
    private List<T> content;
    private  int pageNumber;
    private int pageSize;
    private int totalPages;
    private long totalElements;
    private boolean last;
}
