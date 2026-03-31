package com.me.kaffe.dto.request;

import com.me.kaffe.dto.request.base.BaseRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * CreateCategoryRequest DTO
 * Used for creating a new category
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CreateCategoryRequest extends BaseRequest {

    private String name;
    private String description;

    @Override
    public void validate() {
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

