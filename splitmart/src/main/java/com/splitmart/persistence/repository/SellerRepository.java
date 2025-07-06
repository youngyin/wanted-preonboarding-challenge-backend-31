package com.splitmart.persistence.repository;

import com.splitmart.persistence.entity.Seller;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SellerRepository extends JpaRepository<Seller, Long> {
    
    Optional<Seller> findByName(String name);
    
    List<Seller> findByStatus(String status);
    
    boolean existsByName(String name);
} 