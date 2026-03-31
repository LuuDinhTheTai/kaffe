package com.me.kaffe.dto.request;

import com.me.kaffe.dto.request.base.BaseRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

/**
 * UpdateCategoryRequest DTO
 * Used for updating an existing category
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UpdateCategoryRequest extends BaseRequest {

    private UUID uniqueId;
    private String name;
    private String description;

    @Override
    public void validate() {
        if (uniqueId == null) {
            throw new IllegalArgumentException("Category ID is required");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Category name is required");
        }
        if (name.length() > 100) {
            throw new IllegalArgumentException("Category name must not exceed 100 characters");
        }
        if (description != null && description.length() > 500) {
            throw new IllegalArgumentException("Description must not exceed 500 characters");
        }
    }
}

