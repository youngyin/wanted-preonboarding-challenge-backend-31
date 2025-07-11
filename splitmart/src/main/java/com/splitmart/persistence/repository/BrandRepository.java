package com.splitmart.persistence.repository;

import com.splitmart.persistence.entity.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Long> {
    
    Optional<Brand> findByName(String name);
    
    List<Brand> findByStatus(String status);
    
    boolean existsByName(String name);
} 