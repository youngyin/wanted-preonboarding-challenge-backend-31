package com.splitmart.service;

import com.splitmart.command.*;
import com.splitmart.model.entity.*;
import com.splitmart.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductRegisterService {
    
    private final ProductRepository productRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final ProductTagRepository productTagRepository;
    private final SellerRepository sellerRepository;
    private final BrandRepository brandRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;

    /**
     * create product
     * @param command
     * @return
     */
    @Transactional
    public Long createProduct(CreateProductCommand command) {
        log.info("Creating product with command: {}", command);
        
        // 1. Validation
        if (!command.isValid()) {
            throw new IllegalArgumentException("Invalid product command");
        }
        
        // 2. 판매자, 브랜드 조회 및 검증
        Seller seller = sellerRepository.findById(command.getSellerId())
                .orElseThrow(() -> new IllegalArgumentException("Seller not found with id: " + command.getSellerId()));
        
        Brand brand = null;
        if (command.getBrandId() != null) {
            brand = brandRepository.findById(command.getBrandId())
                    .orElseThrow(() -> new IllegalArgumentException("Brand not found with id: " + command.getBrandId()));
        }
        
        // 3. 슬러그 중복 검증
        if (productRepository.existsBySlug(command.getSlug())) {
            throw new IllegalArgumentException("Product slug already exists: " + command.getSlug());
        }

        // 4. 상품 엔티티 생성 및 저장
        Product product = Product.of(command, brand, seller, "ACTIVE");
        Product savedProduct = productRepository.save(product);
        log.info("Product saved with id: {}", savedProduct.getId());

        // 5. category 매핑
        saveProductCategories(savedProduct, command.getCategoryIds());
        
        // 10. 태그 매핑
        saveProductTags(savedProduct, command.getTagIds());
        
        log.info("Product creation completed successfully with id: {}", savedProduct.getId());
        return savedProduct.getId();
    }

    /**
     * save product categories
     * @param product
     * @param categoryIds
     */
    private void saveProductCategories(Product product, List<Long> categoryIds) {
        List<ProductCategory> productCategories = new ArrayList<>();
        for (Long categoryId : categoryIds) {
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new IllegalArgumentException("Category not found with id: " + categoryId));
            productCategories.add(ProductCategory.of(category, product));
        }

        productCategoryRepository.saveAll(productCategories);
        log.info("Product categories saved successfully for id: {}", product.getId());
    }

    /**
     * save product tags
     * @param product
     * @param tagIds
     */
    private void saveProductTags(Product product, List<Long> tagIds) {
        List<ProductTag> productTags = new ArrayList<>();
        for (Long tagId : tagIds) {
            Tag tag = tagRepository.findById(tagId)
                    .orElseThrow(() -> new IllegalArgumentException("Tag not found with id: " + tagId));
            
            ProductTag productTag = ProductTag.of(tag, product);
            productTags.add(productTag);
        }

        productTagRepository.saveAll(productTags);
        log.info("Product tags saved successfully for id: {}", product.getId());
    }
} 