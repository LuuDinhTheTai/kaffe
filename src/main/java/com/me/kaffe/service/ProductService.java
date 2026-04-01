package com.me.kaffe.service;

import com.me.kaffe.dto.request.ProductFormRequest;
import com.me.kaffe.entity.Category;
import com.me.kaffe.entity.Product;
import com.me.kaffe.repository.CategoryRepository;
import com.me.kaffe.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Product> findById(UUID id) {
        return productRepository.findById(id);
    }

    @Transactional
    public Product save(Product product) {
        return productRepository.save(product);
    }

    @Transactional
    public Product saveFromForm(ProductFormRequest form) {
        validate(form);

        Product product = form.getUniqueId() == null
                ? new Product()
                : productRepository.findById(form.getUniqueId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        Category category = categoryRepository.findById(form.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        // Keep SKU unique for new or changed SKU.
        if (isSkuTakenByAnother(form.getSku(), form.getUniqueId())) {
            throw new IllegalArgumentException("SKU already exists");
        }

        product.setCategory(category);
        product.setName(form.getName().trim());
        product.setSku(form.getSku().trim());
        product.setSize(form.getSize() == null ? null : form.getSize().trim());
        product.setImage(form.getImage() == null ? null : form.getImage().trim());
        product.setDescription(form.getDescription() == null ? null : form.getDescription().trim());

        Product saved = productRepository.save(product);
        log.info("Saved product {} ({})", saved.getName(), saved.getUniqueId());
        return saved;
    }

    @Transactional
    public void deleteById(UUID id) {
        productRepository.deleteById(id);
        log.info("Deleted product {}", id);
    }

    @Transactional(readOnly = true)
    public boolean existsBySku(String sku) {
        return sku != null && productRepository.existsBySkuIgnoreCase(sku.trim());
    }

    @Transactional(readOnly = true)
    public long countByCategoryId(UUID categoryId) {
        return productRepository.countByCategory_UniqueId(categoryId);
    }

    @Transactional(readOnly = true)
    public List<Product> findByCategoryId(UUID categoryId) {
        return productRepository.findByCategory_UniqueId(categoryId);
    }

    private boolean isSkuTakenByAnother(String sku, UUID currentId) {
        return productRepository.findBySkuIgnoreCase(sku.trim())
                .map(existing -> currentId == null || !existing.getUniqueId().equals(currentId))
                .orElse(false);
    }

    private void validate(ProductFormRequest form) {
        if (form.getCategoryId() == null) {
            throw new IllegalArgumentException("Category is required");
        }
        if (form.getName() == null || form.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Product name is required");
        }
        if (form.getSku() == null || form.getSku().trim().isEmpty()) {
            throw new IllegalArgumentException("SKU is required");
        }
        if (form.getName().trim().length() > 120) {
            throw new IllegalArgumentException("Product name must not exceed 120 characters");
        }
        if (form.getSku().trim().length() > 64) {
            throw new IllegalArgumentException("SKU must not exceed 64 characters");
        }
        if (form.getDescription() != null && form.getDescription().length() > 1000) {
            throw new IllegalArgumentException("Description must not exceed 1000 characters");
        }
    }
}
