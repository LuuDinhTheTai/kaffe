package com.me.kaffe.controller;

import com.me.kaffe.entity.CartProduct;
import com.me.kaffe.service.CartProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/cart-products")
@RequiredArgsConstructor
public class CartProductController {

    private final CartProductService cartProductService;

    @GetMapping
    public List<CartProduct> getAll() {
        return cartProductService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CartProduct> getById(@PathVariable UUID id) {
        return cartProductService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public CartProduct create(@RequestBody CartProduct cartProduct) {
        return cartProductService.save(cartProduct);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CartProduct> update(@PathVariable UUID id, @RequestBody CartProduct cartProduct) {
        return cartProductService.findById(id)
                .map(existing -> {
                    cartProduct.setUniqueId(id);
                    return ResponseEntity.ok(cartProductService.save(cartProduct));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        return cartProductService.findById(id)
                .map(existing -> {
                    cartProductService.deleteById(id);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
