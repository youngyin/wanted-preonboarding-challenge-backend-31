package com.splitmart.persistence.repository;

import com.splitmart.persistence.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    
    List<Category> findByParentIdOrderByDisplayOrder(Long parentId);
    
    List<Category> findByLevelOrderByDisplayOrder(Integer level);
    
    List<Category> findByStatus(String status);
    
    List<Category> findByStatusOrderByDisplayOrder(String status);

    List<Category> findAllByIdIn(List<Long> ids);
} 