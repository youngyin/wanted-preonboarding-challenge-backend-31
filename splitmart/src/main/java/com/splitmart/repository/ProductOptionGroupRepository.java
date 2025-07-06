package com.splitmart.repository;

import com.splitmart.model.entity.ProductOptionGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductOptionGroupRepository extends JpaRepository<ProductOptionGroup, Long> {
    
    List<ProductOptionGroup> findByProductIdOrderByDisplayOrder(Long productId);
    
    void deleteByProductId(Long productId);
} 