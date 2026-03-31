package com.me.kaffe.dto.request;

import lombok.Data;

import java.util.UUID;

@Data
public class ProductFormRequest {
    private UUID uniqueId;
    private UUID categoryId;
    private String name;
    private String sku;
    private String size;
    private String image;
    private String description;
}

