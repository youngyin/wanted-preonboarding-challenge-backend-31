package com.splitmart.application.command;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeleteProductCommand {
    
    private Long productId;

    public static DeleteProductCommand of(Long productId) {
        return productId != null ? new DeleteProductCommand(productId) : null;
    }

    // Validation 메서드
    public boolean isValid() {
        return productId != null && productId > 0;
    }
} 