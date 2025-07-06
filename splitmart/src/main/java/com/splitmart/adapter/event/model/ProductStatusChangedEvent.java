package com.splitmart.adapter.event.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * 상품 상태 변경 이벤트
 * 상품의 상태가 변경되었을 때 발행되는 이벤트
 */
@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ProductStatusChangedEvent {

    /**
     * 상품 ID
     */
    private Long productId;

    /**
     * 이전 상태
     */
    private String previousStatus;

    /**
     * 새로운 상태
     */
    private String newStatus;

    /**
     * 상태 변경 사유
     */
    private String reason;

    /**
     * 상태 변경 시각
     */
    private LocalDateTime changedAt;

    /**
     * 변경자 ID (관리자 계정 등, optional)
     */
    private Long changedBy;
} 