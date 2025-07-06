package com.splitmart.persistence.repository;

import com.splitmart.persistence.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    Optional<Product> findBySlug(String slug);
    
    boolean existsBySlug(String slug);
    
    List<Product> findBySellerId(Long sellerId);
    
    List<Product> findByBrandId(Long brandId);
    
    List<Product> findByStatus(String status);
    
    @Query("SELECT p FROM Product p WHERE p.name LIKE %:keyword% OR p.shortDescription LIKE %:keyword%")
    List<Product> findByKeyword(@Param("keyword") String keyword);
    
    @Query("SELECT p FROM Product p WHERE p.seller.id = :sellerId AND p.status = :status")
    List<Product> findBySellerIdAndStatus(@Param("sellerId") Long sellerId, @Param("status") String status);
} 