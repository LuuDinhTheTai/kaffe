package com.me.kaffe.controller;

import com.me.kaffe.dto.request.ProductFormRequest;
import com.me.kaffe.entity.Product;
import com.me.kaffe.service.CategoryService;
import com.me.kaffe.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

@Slf4j
@Controller
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final CategoryService categoryService;

    @GetMapping
    public String listProducts(Model model) {
        model.addAttribute("products", productService.findAll());
        model.addAttribute("pageTitle", "Products");
        return "products";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("productForm", new ProductFormRequest());
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("isNew", true);
        model.addAttribute("pageTitle", "Create Product");
        return "product-form";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable UUID id, Model model) {
        return productService.findById(id)
                .map(product -> {
                    model.addAttribute("productForm", toForm(product));
                    model.addAttribute("categories", categoryService.findAll());
                    model.addAttribute("isNew", false);
                    model.addAttribute("pageTitle", "Edit Product");
                    return "product-form";
                })
                .orElse("redirect:/products?error=Product+not+found");
    }

    @PostMapping
    public String saveProduct(@ModelAttribute("productForm") ProductFormRequest form, Model model) {
        try {
            productService.saveFromForm(form);
            return "redirect:/products?success=true";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("categories", categoryService.findAll());
            model.addAttribute("isNew", form.getUniqueId() == null);
            model.addAttribute("pageTitle", form.getUniqueId() == null ? "Create Product" : "Edit Product");
            return "product-form";
        }
    }

    @GetMapping("/{id}/delete")
    public String deleteProduct(@PathVariable UUID id) {
        try {
            productService.deleteById(id);
            return "redirect:/products?success=true";
        } catch (Exception e) {
            log.error("Failed to delete product {}", id, e);
            return "redirect:/products?error=Failed+to+delete+product";
        }
    }

    @GetMapping("/{id}")
    public String productDetail(@PathVariable UUID id, Model model) {
        return productService.findById(id)
                .map(product -> {
                    model.addAttribute("product", product);
                    model.addAttribute("pageTitle", product.getName());
                    return "product";
                })
                .orElse("redirect:/products?error=Product+not+found");
    }

    private ProductFormRequest toForm(Product product) {
        ProductFormRequest form = new ProductFormRequest();
        form.setUniqueId(product.getUniqueId());
        if (product.getCategory() != null) {
            form.setCategoryId(product.getCategory().getUniqueId());
        }
        form.setName(product.getName());
        form.setSku(product.getSku());
        form.setSize(product.getSize());
        form.setImage(product.getImage());
        form.setDescription(product.getDescription());
        return form;
    }
}
