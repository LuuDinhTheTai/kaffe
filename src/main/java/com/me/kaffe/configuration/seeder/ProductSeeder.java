package com.me.kaffe.configuration.seeder;

import com.me.kaffe.entity.Category;
import com.me.kaffe.entity.Product;
import com.me.kaffe.repository.CategoryRepository;
import com.me.kaffe.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(2)
public class ProductSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(ProductSeeder.class);

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductSeeder(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void run(String... args) {
        seed("ESP-001", "Classic Espresso", "Espresso", "Single", "", "Rich and intense espresso shot.");
        seed("CB-001", "Signature Cold Brew", "Cold Brew", "Medium", "", "Smooth cold brew steeped for 18 hours.");
        seed("SGN-001", "Caramel Latte", "Signature", "Large", "", "Espresso, milk, and caramel syrup.");
    }

    private void seed(String sku, String name, String categoryName, String size, String image, String description) {
        if (productRepository.existsBySkuIgnoreCase(sku)) {
            log.debug("Product already exists, skipping seeding: {}", sku);
            return;
        }

        Category category = categoryRepository.findByNameIgnoreCase(categoryName)
                .orElse(null);

        if (category == null) {
            log.warn("Category '{}' missing, skipping product seed {}", categoryName, sku);
            return;
        }

        Product product = new Product();
        product.setSku(sku);
        product.setName(name);
        product.setCategory(category);
        product.setSize(size);
        product.setImage(image);
        product.setDescription(description);

        productRepository.save(product);
        log.info("Seeded product: {} ({})", name, sku);
    }
}

