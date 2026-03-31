package com.me.kaffe.service;

import com.me.kaffe.entity.Category;
import com.me.kaffe.repository.CategoryRepository;
import com.me.kaffe.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * CategoryService - Category Management Service Layer
 * Handles business logic for category CRUD operations
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    /**
     * Get all categories
     */
    @Transactional(readOnly = true)
    public List<Category> findAll() {
        log.debug("Retrieving all categories");
        return categoryRepository.findAll();
    }

    /**
     * Get category by ID
     */
    @Transactional(readOnly = true)
    public Optional<Category> findById(UUID id) {
        log.debug("Retrieving category with ID: {}", id);
        return categoryRepository.findById(id);
    }

    /**
     * Create or update category
     */
    @Transactional
    public Category save(Category category) {
        if (category.getName() == null || category.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Category name is required");
        }

        if (category.getName().trim().length() > 100) {
            throw new IllegalArgumentException("Category name must not exceed 100 characters");
        }

        if (category.getDescription() != null && category.getDescription().length() > 500) {
            throw new IllegalArgumentException("Description must not exceed 500 characters");
        }

        category.setName(category.getName().trim());
        if (category.getDescription() != null) {
            category.setDescription(category.getDescription().trim());
        }

        if (category.getUniqueId() == null) {
            log.info("Creating new category: {}", category.getName());
        } else {
            log.info("Updating category: {} (ID: {})", category.getName(), category.getUniqueId());
        }

        return categoryRepository.save(category);
    }

    /**
     * Delete category by ID
     */
    @Transactional
    public void deleteById(UUID id) {
        log.info("Deleting category with ID: {}", id);
        if (!categoryRepository.existsById(id)) {
            throw new IllegalArgumentException("Category not found");
        }

        long productCount = productRepository.countByCategory_UniqueId(id);
        if (productCount > 0) {
            throw new IllegalStateException("Cannot delete category with existing products");
        }

        categoryRepository.deleteById(id);
    }

    /**
     * Check if category exists
     */
    @Transactional(readOnly = true)
    public boolean existsById(UUID id) {
        return categoryRepository.existsById(id);
    }

    /**
     * Count total categories
     */
    @Transactional(readOnly = true)
    public long count() {
        return categoryRepository.count();
    }
}
