package com.splitmart.application.service;

import com.splitmart.application.command.UpdateProductCommand;
import com.splitmart.persistence.entity.*;
import com.splitmart.persistence.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductUpdateService {
    
    private final ProductRepository productRepository;
    private final SellerRepository sellerRepository;
    private final BrandRepository brandRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;


    @Transactional
    public void updateProduct(UpdateProductCommand command) {
        log.info("Updating product with command: {}", command);

        // 1. 검증
        if (!command.isValid()) throw new IllegalArgumentException("Invalid product update command");

        // 2. 조회
        Long productId = command.getProductId();
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        Seller seller = sellerRepository.findById(command.getSellerId())
                .orElseThrow(() -> new IllegalArgumentException("Seller not found"));

        Brand brand = command.getBrandId() != null ?
                brandRepository.findById(command.getBrandId())
                        .orElseThrow(() -> new IllegalArgumentException("Brand not found"))
                : null;

        // 3. 기본 정보 업데이트
        product.update(command, brand, seller);

        // 4. 카테고리/태그 삭제 후 재등록
        List<ProductCategory> categories = categoryRepository.findAllByIdIn(command.getCategoryIds()).stream()
                .map(category -> ProductCategory.of(category, product)).toList();
        product.replaceCategories(categories);

        List<ProductTag> tags = tagRepository.findAllById(command.getTagIds()).stream()
                .map(tag -> ProductTag.of(tag, product)).toList();
        product.replaceTags(tags);

        log.info("Product updated successfully: {}", product.getId());
    }

}