package com.me.kaffe.configuration.web;

import com.me.kaffe.entity.Category;
import com.me.kaffe.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalModelAttributes {

    private final CategoryService categoryService;

    @ModelAttribute("navbarCategories")
    public List<Category> navbarCategories() {
        return categoryService.findAll();
    }
}

