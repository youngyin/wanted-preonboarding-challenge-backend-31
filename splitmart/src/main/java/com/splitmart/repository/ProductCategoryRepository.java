package com.splitmart.repository;

import com.splitmart.model.entity.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Long> {
    
    List<ProductCategory> findByProductId(Long productId);
    
    List<ProductCategory> findByCategoryId(Long categoryId);
    
    List<ProductCategory> findByProductIdAndIsPrimaryTrue(Long productId);
    
    void deleteByProductId(Long productId);
    
    void deleteByProductIdAndCategoryId(Long productId, Long categoryId);
} 