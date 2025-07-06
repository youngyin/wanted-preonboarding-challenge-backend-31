package com.splitmart.persistence.repository;

import com.splitmart.persistence.entity.ProductOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductOptionRepository extends JpaRepository<ProductOption, Long> {
    
    List<ProductOption> findByOptionGroupIdOrderByDisplayOrder(Long optionGroupId);
    
    List<ProductOption> findByOptionGroupProductId(Long productId);
    
    void deleteByOptionGroupId(Long optionGroupId);
} 