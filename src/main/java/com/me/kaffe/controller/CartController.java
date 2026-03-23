package com.me.kaffe.controller;

import com.me.kaffe.entity.Cart;
import com.me.kaffe.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/carts")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public List<Cart> getAll() {
        return cartService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cart> getById(@PathVariable UUID id) {
        return cartService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Cart create(@RequestBody Cart cart) {
        return cartService.save(cart);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Cart> update(@PathVariable UUID id, @RequestBody Cart cart) {
        return cartService.findById(id)
                .map(existing -> {
                    cart.setUniqueId(id);
                    return ResponseEntity.ok(cartService.save(cart));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        return cartService.findById(id)
                .map(existing -> {
                    cartService.deleteById(id);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
