package com.splitmart.adapter.controller;

import com.splitmart.application.command.CreateProductCommand;
import com.splitmart.application.command.DeleteProductCommand;
import com.splitmart.application.command.UpdateProductCommand;
import com.splitmart.application.service.ProductDeleteService;
import com.splitmart.application.service.ProductRegisterService;
import com.splitmart.application.service.ProductUpdateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductCommandController {

    private final ProductRegisterService productRegisterService;
    private final ProductUpdateService productUpdateService;
    private final ProductDeleteService productDeleteService;

    @PostMapping
    public ResponseEntity<Map<String, Long>> createProduct(
        @RequestBody CreateProductCommand command
    ) {
        log.info("Received create product request: {}", command.getName());
        
        try {
            Long productId = productRegisterService.createProduct(command);
            log.info("Product created successfully with id: {}", productId);
            
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("productId", productId));
                    
        } catch (IllegalArgumentException e) {
            log.warn("Invalid request for product creation: {}", e.getMessage());
            throw e;
        }
    }

    @PutMapping("/{productId}")
    public ResponseEntity<Void> updateProduct(
        @PathVariable Long productId,
        @RequestBody UpdateProductCommand command
    ) {
        log.info("Received update product request for id: {}", productId);
        
        // productId를 command에 설정
        command.setProductId(productId);
        
        try {
            productUpdateService.updateProduct(command);
            log.info("Product updated successfully for id: {}", productId);
            
            return ResponseEntity.ok().build();
            
        } catch (IllegalArgumentException e) {
            log.warn("Invalid request for product update: {}", e.getMessage());
            throw e;
        }
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(
        @PathVariable Long productId
    ) {
        log.info("Received delete product request for id: {}", productId);
        
        DeleteProductCommand command = DeleteProductCommand.builder()
                .productId(productId)
                .build();
        
        try {
            productDeleteService.deleteProduct(command);
            log.info("Product deleted successfully for id: {}", productId);
            
            return ResponseEntity.noContent().build();
            
        } catch (IllegalArgumentException e) {
            log.warn("Invalid request for product deletion: {}", e.getMessage());
            throw e;
        }
    }
} 