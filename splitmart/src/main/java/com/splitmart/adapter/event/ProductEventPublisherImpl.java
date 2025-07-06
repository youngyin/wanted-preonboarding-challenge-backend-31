package com.splitmart.adapter.event;

import com.splitmart.adapter.event.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductEventPublisherImpl implements ProductEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String TOPIC_PRODUCT_CREATED = "product.created";
    private static final String TOPIC_PRODUCT_UPDATED = "product.updated";
    private static final String TOPIC_PRODUCT_DELETED = "product.deleted";
    private static final String TOPIC_PRODUCT_STATUS_CHANGED = "product.status.changed";
    private static final String TOPIC_PRODUCT_PRICE_CHANGED = "product.price.changed";

    @Override
    public void publishProductCreated(ProductCreatedEvent event) {
        log.info("Publishing ProductCreatedEvent: {}", event);
        kafkaTemplate.send(TOPIC_PRODUCT_CREATED, event.getProductId().toString(), event);
    }

    @Override
    public void publishProductUpdated(ProductUpdatedEvent event) {
        log.info("Publishing ProductUpdatedEvent: {}", event);
        kafkaTemplate.send(TOPIC_PRODUCT_UPDATED, event.getProductId().toString(), event);
    }

    @Override
    public void publishProductDeleted(ProductDeletedEvent event) {
        log.info("Publishing ProductDeletedEvent: {}", event);
        kafkaTemplate.send(TOPIC_PRODUCT_DELETED, event.getProductId().toString(), event);
    }

    @Override
    public void publishProductStatusChanged(ProductStatusChangedEvent event) {
        log.info("Publishing ProductStatusChangedEvent: {}", event);
        kafkaTemplate.send(TOPIC_PRODUCT_STATUS_CHANGED, event.getProductId().toString(), event);
    }

    @Override
    public void publishProductPriceChanged(ProductPriceChangedEvent event) {
        log.info("Publishing ProductPriceChangedEvent: {}", event);
        kafkaTemplate.send(TOPIC_PRODUCT_PRICE_CHANGED, event.getProductId().toString(), event);
    }
}