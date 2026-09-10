package com.ayushcart.dto;

import org.springframework.data.domain.Page;

import java.util.List;

/**
 * A stable JSON shape for paginated results. Serializing Spring's Page directly
 * is discouraged because its JSON structure is not guaranteed between versions.
 */
public record PageResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean last
) {
    public static <T> PageResponse<T> from(Page<T> page) {
        return new PageResponse<>(page.getContent(), page.getNumber(), page.getSize(),
                page.getTotalElements(), page.getTotalPages(), page.isLast());
    }
}
