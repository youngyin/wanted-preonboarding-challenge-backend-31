package com.splitmart.persistence.repository;

import com.splitmart.persistence.entity.ProductTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductTagRepository extends JpaRepository<ProductTag, Long> {
    
    List<ProductTag> findByProductId(Long productId);
    
    List<ProductTag> findByTagId(Long tagId);
    
    void deleteByProductId(Long productId);
    
    void deleteByProductIdAndTagId(Long productId, Long tagId);
} 