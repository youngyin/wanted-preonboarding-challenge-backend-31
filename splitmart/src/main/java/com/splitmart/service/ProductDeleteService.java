package com.splitmart.service;

import com.splitmart.command.*;
import com.splitmart.repository.*;
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

    @Transactional
    public void deleteProduct(DeleteProductCommand command) {
        log.info("Deleting product with command: {}", command);

        // 1. Validation
        if (!command.isValid()) {
            throw new IllegalArgumentException("Invalid delete product command");
        }

        // 2. 상품 존재 여부 확인
        if (!productRepository.existsById(command.getProductId())) {
            throw new IllegalArgumentException("Product not found with id: " + command.getProductId());
        }

        // 4. 상품 삭제
        productRepository.deleteById(command.getProductId());

        log.info("Product deletion completed successfully for id: {}", command.getProductId());
    }
}