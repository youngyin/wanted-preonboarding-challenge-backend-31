package com.splitmart.adapter.event.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 상품 생성 이벤트
 * 상품이 성공적으로 생성되었을 때 발행되는 이벤트
 */
@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ProductCreatedEvent {

    /**
     * 생성된 상품의 ID
     */
    private Long productId;

    /**
     * 상품명
     */
    private String name;

    /**
     * URL-friendly 식별자
     */
    private String slug;

    /**
     * 기본 가격
     */
    private BigDecimal basePrice;

    /**
     * 판매 가격
     */
    private BigDecimal salePrice;

    /**
     * 통화 (예: KRW)
     */
    private String currency;

    /**
     * 브랜드 ID
     */
    private Long brandId;

    /**
     * 판매자 ID
     */
    private Long sellerId;

    /**
     * 생성 시각
     */
    private LocalDateTime createdAt;
} 