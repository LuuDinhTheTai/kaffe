package com.me.kaffe.repository;

import com.me.kaffe.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {
    boolean existsBySkuIgnoreCase(String sku);

    Optional<Product> findBySkuIgnoreCase(String sku);

    long countByCategory_UniqueId(UUID categoryId);

    List<Product> findByCategory_UniqueId(UUID categoryId);
}
