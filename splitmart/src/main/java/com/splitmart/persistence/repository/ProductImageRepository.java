package com.splitmart.persistence.repository;

import com.splitmart.persistence.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
    
    List<ProductImage> findByProductIdOrderByDisplayOrder(Long productId);
    
    List<ProductImage> findByProductIdAndIsPrimaryTrue(Long productId);
    
    List<ProductImage> findByOptionId(Long optionId);
    
    void deleteByProductId(Long productId);
} 