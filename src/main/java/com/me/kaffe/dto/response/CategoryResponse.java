package com.me.kaffe.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * CategoryResponse DTO
 * Used for returning category information to clients
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryResponse {

    private UUID uniqueId;
    private String name;
    private String description;
    private Integer productCount;

    /**
     * Create CategoryResponse from basic info
     */
    public static CategoryResponse of(UUID uniqueId, String name, String description) {
        return CategoryResponse.builder()
                .uniqueId(uniqueId)
                .name(name)
                .description(description)
                .productCount(0)
                .build();
    }

    /**
     * Create CategoryResponse with product count
     */
    public static CategoryResponse of(UUID uniqueId, String name, String description, Integer productCount) {
        return CategoryResponse.builder()
                .uniqueId(uniqueId)
                .name(name)
                .description(description)
                .productCount(productCount)
                .build();
    }
}

