package com.splitmart.application.command;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class UpdateProductCommand {
    
    private Long productId;
    private String name;
    private String slug;
    private String shortDescription;
    private String fullDescription;
    private Long sellerId;
    private Long brandId;
    private BigDecimal basePrice;
    private BigDecimal salePrice;
    private List<Long> categoryIds;
    private List<Long> tagIds;
    private List<OptionGroupCommand> optionGroups;
    private ProductDetailCommand productDetail;
    private List<ProductImageCommand> images;
    
    // Validation 메서드
    public boolean isValid() {
        return productId != null &&
               name != null && !name.trim().isEmpty() &&
               slug != null && !slug.trim().isEmpty() &&
               sellerId != null &&
               basePrice != null && basePrice.compareTo(BigDecimal.ZERO) >= 0;
    }
    
    // 기본값 설정
    public BigDecimal getSalePrice() {
        return salePrice != null ? salePrice : basePrice;
    }
    
    public List<Long> getCategoryIds() {
        return categoryIds != null ? categoryIds : List.of();
    }
    
    public List<Long> getTagIds() {
        return tagIds != null ? tagIds : List.of();
    }
    
    public List<OptionGroupCommand> getOptionGroups() {
        return optionGroups != null ? optionGroups : List.of();
    }
    
    public List<ProductImageCommand> getImages() {
        return images != null ? images : List.of();
    }
} 