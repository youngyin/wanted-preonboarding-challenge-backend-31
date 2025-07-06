package com.splitmart.persistence.repository;

import com.splitmart.persistence.entity.ProductDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductDetailRepository extends JpaRepository<ProductDetail, Long> {
    
    Optional<ProductDetail> findByProductId(Long productId);
    
    void deleteByProductId(Long productId);
} 