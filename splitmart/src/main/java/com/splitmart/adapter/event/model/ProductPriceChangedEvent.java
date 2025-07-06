package com.splitmart.adapter.event.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 상품 가격 변경 이벤트
 * 상품의 가격이 변경되었을 때 발행되는 이벤트
 */
@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ProductPriceChangedEvent {

    /**
     * 상품 ID
     */
    private Long productId;

    /**
     * 이전 기본 가격
     */
    private BigDecimal previousBasePrice;

    /**
     * 새로운 기본 가격
     */
    private BigDecimal newBasePrice;

    /**
     * 이전 판매 가격
     */
    private BigDecimal previousSalePrice;

    /**
     * 새로운 판매 가격
     */
    private BigDecimal newSalePrice;

    /**
     * 통화
     */
    private String currency;

    /**
     * 가격 변경 사유
     */
    private String reason;

    /**
     * 가격 변경 시각
     */
    private LocalDateTime changedAt;

    /**
     * 변경자 ID (관리자 계정 등, optional)
     */
    private Long changedBy;
} 