package com.me.kaffe.controller;

import com.me.kaffe.entity.Category;
import com.me.kaffe.service.CategoryService;
import com.me.kaffe.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * CategoryController - Category Management Controller (MVC with Thymeleaf)
 * Handles web UI endpoints for category management using Model-View-Controller pattern
 */
@Slf4j
@Controller
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
    private final ProductService productService;

    /**
     * GET /categories - Display all categories
     * Shows category management page with list of all categories
     */
    @GetMapping
    public String listCategories(Model model) {
        log.info("Displaying categories page");

        List<Category> categories = categoryService.findAll();
        Map<UUID, Long> productCounts = new HashMap<>();
        for (Category category : categories) {
            productCounts.put(category.getUniqueId(), productService.countByCategoryId(category.getUniqueId()));
        }

        model.addAttribute("categories", categories);
        model.addAttribute("productCounts", productCounts);
        model.addAttribute("pageTitle", "Categories");
        model.addAttribute("totalCategories", categories.size());
        return "categories";
    }

    /**
     * GET /categories/new - Display create category form
     * Shows empty form for creating a new category
     */
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        log.info("Displaying create category form");

        model.addAttribute("category", new Category());
        model.addAttribute("pageTitle", "Create New Category");
        model.addAttribute("isNew", true);
        return "category-form";
    }

    /**
     * GET /categories/{id}/edit - Display edit category form
     * Shows form pre-populated with existing category data
     */
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable UUID id, Model model) {
        log.info("Displaying edit form for category: {}", id);

        return categoryService.findById(id)
                .map(category -> {
                    model.addAttribute("category", category);
                    model.addAttribute("pageTitle", "Edit Category");
                    model.addAttribute("isNew", false);
                    return "category-form";
                })
                .orElse("redirect:/categories?error=Category+not+found");
    }

    /**
     * POST /categories - Save category (create or update)
     * Processes form submission and saves category to database
     */
    @PostMapping
    public String saveCategory(@ModelAttribute("category") Category category, Model model) {
        log.info("Saving category: {}", category.getName());

        try {
            // Validation
            if (category.getName() == null || category.getName().trim().isEmpty()) {
                log.warn("Validation failed: Category name is required");
                model.addAttribute("category", category);
                model.addAttribute("error", "Category name is required");
                model.addAttribute("pageTitle", category.getUniqueId() == null ? "Create New Category" : "Edit Category");
                model.addAttribute("isNew", category.getUniqueId() == null);
                return "category-form";
            }

            if (category.getName().length() > 100) {
                log.warn("Validation failed: Category name too long");
                model.addAttribute("category", category);
                model.addAttribute("error", "Category name must not exceed 100 characters");
                model.addAttribute("pageTitle", category.getUniqueId() == null ? "Create New Category" : "Edit Category");
                model.addAttribute("isNew", category.getUniqueId() == null);
                return "category-form";
            }

            if (category.getDescription() != null && category.getDescription().length() > 500) {
                log.warn("Validation failed: Description too long");
                model.addAttribute("category", category);
                model.addAttribute("error", "Description must not exceed 500 characters");
                model.addAttribute("pageTitle", category.getUniqueId() == null ? "Create New Category" : "Edit Category");
                model.addAttribute("isNew", category.getUniqueId() == null);
                return "category-form";
            }

            // Save category
            categoryService.save(category);

            if (category.getUniqueId() == null) {
                log.info("New category created successfully");
            } else {
                log.info("Category updated successfully: {}", category.getUniqueId());
            }

            return "redirect:/categories?success=true";

        } catch (IllegalArgumentException e) {
            model.addAttribute("category", category);
            model.addAttribute("error", e.getMessage());
            model.addAttribute("pageTitle", category.getUniqueId() == null ? "Create New Category" : "Edit Category");
            model.addAttribute("isNew", category.getUniqueId() == null);
            return "category-form";
        } catch (Exception e) {
            log.error("Error saving category", e);
            model.addAttribute("category", category);
            model.addAttribute("error", "Failed to save category: " + e.getMessage());
            model.addAttribute("pageTitle", category.getUniqueId() == null ? "Create New Category" : "Edit Category");
            model.addAttribute("isNew", category.getUniqueId() == null);
            return "category-form";
        }
    }

    /**
     * GET /categories/{id}/delete - Delete category
     * Removes category from database and redirects to list
     */
    @GetMapping("/{id}/delete")
    public String deleteCategory(@PathVariable UUID id) {
        log.info("Deleting category: {}", id);

        try {
            categoryService.deleteById(id);
            log.info("Category deleted successfully");
            return "redirect:/categories?success=true";
        } catch (Exception e) {
            log.warn("Failed to delete category {}: {}", id, e.getMessage());
            String message = URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
            return "redirect:/categories?error=" + message;
        }
    }

    /**
     * GET /categories/{id} - Display category details
     * Shows category information and associated products
     */
    @GetMapping("/{id}")
    public String categoryDetail(@PathVariable UUID id, Model model) {
        return categoryService.findById(id)
                .map(category -> {
                    model.addAttribute("category", category);
                    model.addAttribute("products", productService.findByCategoryId(id));
                    model.addAttribute("pageTitle", category.getName());
                    return "category";
                })
                .orElse("redirect:/categories?error=Category+not+found");
    }
}
