package com.splitmart.repository;

import com.splitmart.model.entity.ProductPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductPriceRepository extends JpaRepository<ProductPrice, Long> {
    
    Optional<ProductPrice> findByProductId(Long productId);
    
    void deleteByProductId(Long productId);
} 