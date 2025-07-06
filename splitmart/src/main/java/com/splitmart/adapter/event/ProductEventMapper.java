package com.splitmart.adapter.event;

import com.splitmart.adapter.event.model.*;
import com.splitmart.persistence.entity.Product;
import com.splitmart.persistence.entity.ProductPrice;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class ProductEventMapper {

    public static ProductCreatedEvent toCreatedEvent(Product product) {
        return ProductCreatedEvent.builder()
                .productId(product.getId())
                .name(product.getName())
                .slug(product.getSlug())
                .basePrice(product.getPrice().getBasePrice())
                .salePrice(product.getPrice().getSalePrice())
                .createdAt(product.getCreatedAt())
                .build();
    }

    public static ProductUpdatedEvent toUpdatedEvent(Product product) {
        return ProductUpdatedEvent.builder()
                .productId(product.getId())
                .name(product.getName())
                .slug(product.getSlug())
                .updatedAt(product.getUpdatedAt())
                .build();
    }

    public static ProductDeletedEvent toDeletedEvent(Long productId) {
        return ProductDeletedEvent.builder()
                .productId(productId)
                .build();
    }

    public static ProductStatusChangedEvent toStatusChangedEvent(Product product, String oldStatus) {
        return ProductStatusChangedEvent.builder()
                .productId(product.getId())
                .previousStatus(oldStatus)
                .newStatus(product.getStatus())
                .changedAt(product.getUpdatedAt())
                .reason("자동 상태 변경")
                .changedBy(null)
                .build();
    }

    public static ProductPriceChangedEvent toPriceChangedEvent(Product product, ProductPrice oldPrice) {
        return ProductPriceChangedEvent.builder()
                .productId(product.getId())
                .newBasePrice(product.getPrice().getBasePrice())
                .newSalePrice(product.getPrice().getSalePrice())
                .previousBasePrice(oldPrice.getBasePrice())
                .previousSalePrice(oldPrice.getSalePrice())
                .changedAt(product.getUpdatedAt())
                .build();
    }
}
