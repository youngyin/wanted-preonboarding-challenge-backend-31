package com.splitmart.application.service;

import com.splitmart.adapter.event.ProductEventMapper;
import com.splitmart.adapter.event.ProductEventPublisher;
import com.splitmart.adapter.event.model.ProductDeletedEvent;
import com.splitmart.application.command.DeleteProductCommand;
import com.splitmart.persistence.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductDeleteService {
    private final ProductRepository productRepository;
    private final ProductEventPublisher eventPublisher;

    @Transactional
    public void deleteProduct(DeleteProductCommand command) {
        log.info("Deleting product with command: {}", command);

        // 1. Validation
        if (!command.isValid()) {
            throw new IllegalArgumentException("Invalid delete product command");
        }

        // 2. 상품 존재 여부 확인
        Long productId = command.getProductId();
        if (!productRepository.existsById(productId)) {
            throw new IllegalArgumentException("Product not found with id: " + productId);
        }

        // 4. 상품 삭제
        productRepository.deleteById(productId);

        // 5. event 발행
        ProductDeletedEvent event = ProductEventMapper.toDeletedEvent(productId);
        eventPublisher.publishProductDeleted(event);

        log.info("Product deletion completed successfully for id: {}", productId);
    }
}