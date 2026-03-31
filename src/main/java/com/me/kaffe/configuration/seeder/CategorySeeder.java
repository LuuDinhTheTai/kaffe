package com.me.kaffe.configuration.seeder;

import com.me.kaffe.entity.Category;
import com.me.kaffe.repository.CategoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1)
public class CategorySeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(CategorySeeder.class);

    private final CategoryRepository categoryRepository;

    public CategorySeeder(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void run(String... args) {
        seed("Espresso", "Strong coffee brewed by forcing hot water through fine grounds.");
        seed("Manual Brew", "Slow brew methods like V60, Chemex, and Kalita.");
        seed("Cold Brew", "Coffee steeped in cold water for smooth and low acidity taste.");
        seed("Signature", "House-special drinks with unique flavors.");
    }

    private void seed(String name, String description) {
        if (categoryRepository.existsByNameIgnoreCase(name)) {
            log.debug("Category already exists, skipping seeding: {}", name);
            return;
        }

        Category category = new Category();
        category.setName(name);
        category.setDescription(description);
        categoryRepository.save(category);

        log.info("Seeded category: {}", name);
    }
}
