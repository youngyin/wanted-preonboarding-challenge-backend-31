package com.splitmart.adapter.event;

import com.splitmart.adapter.event.model.*;

/**
 * 상품 이벤트 발행 인터페이스
 * 상품 관련 이벤트들을 발행하는 역할을 담당
 */
public interface ProductEventPublisher {

    /**
     * 상품 생성 이벤트 발행
     * @param event 상품 생성 이벤트
     */
    void publishProductCreated(ProductCreatedEvent event);

    /**
     * 상품 수정 이벤트 발행
     * @param event 상품 수정 이벤트
     */
    void publishProductUpdated(ProductUpdatedEvent event);

    /**
     * 상품 삭제 이벤트 발행
     * @param event 상품 삭제 이벤트
     */
    void publishProductDeleted(ProductDeletedEvent event);

    /**
     * 상품 상태 변경 이벤트 발행
     * @param event 상품 상태 변경 이벤트
     */
    void publishProductStatusChanged(ProductStatusChangedEvent event);

    /**
     * 상품 가격 변경 이벤트 발행
     * @param event 상품 가격 변경 이벤트
     */
    void publishProductPriceChanged(ProductPriceChangedEvent event);
} 