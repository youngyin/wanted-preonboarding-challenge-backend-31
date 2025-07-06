package com.splitmart.adapter.event.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 상품 수정 이벤트
 * 상품이 성공적으로 수정되었을 때 발행되는 이벤트
 */
@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ProductUpdatedEvent {

    /**
     * 수정된 상품의 ID
     */
    private Long productId;

    /**
     * 변경된 상품명 (선택적)
     */
    private String name;

    /**
     * 변경된 슬러그 (선택적)
     */
    private String slug;

    /**
     * 변경된 기본 가격 (선택적)
     */
    private BigDecimal basePrice;

    /**
     * 변경된 판매 가격 (선택적)
     */
    private BigDecimal salePrice;

    /**
     * 통화
     */
    private String currency;

    /**
     * 수정된 시점
     */
    private LocalDateTime updatedAt;

    /**
     * 변경된 필드명 리스트 (예: ["name", "salePrice"])
     * 모든 값이 업데이트되지는 않더라도 full payload를 보낼 수 있음.
     * 변경 필드는 구분 목적으로만 사용.
     */
    private List<String> updatedFields;
} 