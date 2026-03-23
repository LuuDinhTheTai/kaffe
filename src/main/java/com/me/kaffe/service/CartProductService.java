package com.me.kaffe.service;

import com.me.kaffe.entity.CartProduct;
import com.me.kaffe.repository.CartProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CartProductService {

    private final CartProductRepository cartProductRepository;

    public List<CartProduct> findAll() {
        return cartProductRepository.findAll();
    }

    public Optional<CartProduct> findById(UUID id) {
        return cartProductRepository.findById(id);
    }

    public CartProduct save(CartProduct cartProduct) {
        return cartProductRepository.save(cartProduct);
    }

    public void deleteById(UUID id) {
        cartProductRepository.deleteById(id);
    }
}
