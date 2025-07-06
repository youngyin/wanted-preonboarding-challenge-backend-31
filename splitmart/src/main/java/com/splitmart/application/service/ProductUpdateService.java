package com.splitmart.application.service;

import com.splitmart.adapter.event.ProductEventMapper;
import com.splitmart.adapter.event.ProductEventPublisher;
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
public class ProductUpdateService {

    private final ProductRepository productRepository;
    private final SellerRepository sellerRepository;
    private final BrandRepository brandRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;
    private final ProductEventPublisher eventPublisher;

    @Transactional
    public void updateProduct(UpdateProductCommand command) {
        log.info("Updating product with command: {}", command);

        // 1. 검증
        if (!command.isValid()) throw new IllegalArgumentException("Invalid product update command");

        // 2. 기존 상품 조회
        Product product = productRepository.findById(command.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        String oldStatus = product.getStatus();
        var oldPrice = product.getPrice();

        // 3. 연관 엔티티 조회
        Seller seller = sellerRepository.findById(command.getSellerId())
                .orElseThrow(() -> new IllegalArgumentException("Seller not found"));
        Brand brand = command.getBrandId() != null
                ? brandRepository.findById(command.getBrandId())
                .orElseThrow(() -> new IllegalArgumentException("Brand not found"))
                : null;

        // 4. 도메인 객체 업데이트
        product.update(command, brand, seller);

        List<ProductCategory> categories = categoryRepository.findAllByIdIn(command.getCategoryIds()).stream()
                .map(c -> ProductCategory.of(c, product)).toList();
        product.replaceCategories(categories);

        List<ProductTag> tags = tagRepository.findAllById(command.getTagIds()).stream()
                .map(t -> ProductTag.of(t, product)).toList();
        product.replaceTags(tags);

        log.info("Product updated successfully: {}", product.getId());

        // 5. 이벤트 발행
        eventPublisher.publishProductUpdated(ProductEventMapper.toUpdatedEvent(product));

        if (!oldStatus.equals(product.getStatus())) {
            eventPublisher.publishProductStatusChanged(ProductEventMapper.toStatusChangedEvent(product, oldStatus));
        }

        if (oldPrice != null && product.getPrice() != null &&
                oldPrice.getSalePrice().compareTo(product.getPrice().getSalePrice()) != 0) {
            eventPublisher.publishProductPriceChanged(ProductEventMapper.toPriceChangedEvent(product, oldPrice));
        }
    }
}