package com.splitmart.adapter.event.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * 상품 삭제 이벤트
 * 상품이 성공적으로 삭제되었을 때 발행되는 이벤트
 */
@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ProductDeletedEvent {

    /**
     * 삭제된 상품의 ID
     */
    private Long productId;

    /**
     * 삭제 사유 (직접 입력 가능)
     */
    private String reason;

    /**
     * 삭제 시각
     */
    private LocalDateTime deletedAt;

    /**
     * 삭제자 ID (관리자 계정 등, optional)
     */
    private Long deletedBy;
} 