
# 📘 Kafka 기반 이벤트 발행 아키텍처 설계

## 1. 설계 목적

* Command 모델에서 발생한 변경사항을 외부에 전달하기 위한 **이벤트 발행 구조 도입**
* Kafka 기반의 비동기 처리 구조로 **쓰기와 조회 모델의 분리** 실현
* Query 서버 및 외부 Consumer와의 **결합도 최소화**
* 서비스 간 통신을 직접 호출 대신 **이벤트 기반 처리**로 대체

---

## 2. 아키텍처 구성도

```
ProductService (Command)
 ├─ 도메인 객체 수정 (JPA)
 ├─ EventMapper: Entity → Event DTO 변환
 ├─ ProductEventPublisher (interface)
 └─ ProductEventPublisherImpl (Kafka 또는 Logger)
```

* **EventMapper**: Domain Entity를 이벤트 DTO로 변환하는 책임
* **ProductEventPublisher**: 발행 로직의 추상화 (Kafka, RabbitMQ, 기타)
* **ProductEventPublisherImpl**: 실제 발행 구현체 (현재는 로그 기반, Kafka 연동 예정)
* **Event DTO**: 순수 자바 객체로 직렬화 가능하도록 설계됨

---

## 3. 이벤트 클래스 정의

### 3.1 주요 이벤트 목록

| 이벤트 클래스                     | 발생 시점                                |
| --------------------------- | ------------------------------------ |
| `ProductCreatedEvent`       | 상품이 최초로 생성된 경우                       |
| `ProductUpdatedEvent`       | 상품 기본 정보, 상세정보, 이미지 등 수정 시           |
| `ProductDeletedEvent`       | 상품이 삭제된 경우                           |
| `ProductStatusChangedEvent` | 상품 상태(`판매중`, `품절`, `숨김` 등) 변경 시      |
| `ProductPriceChangedEvent`  | 상품 가격(`basePrice`, `salePrice`) 변경 시 |

---

## 4. EventMapper 설계

**EventMapper**는 도메인 객체인 `Product`에서 필요한 정보를 추출해 각 이벤트 DTO로 변환한다.
이벤트 간 중복을 피하고, 필요한 최소 필드만 추출한다.

예시:

```java
public static ProductPriceChangedEvent toPriceChangedEvent(Product product, BigDecimal oldPrice) {
    return ProductPriceChangedEvent.builder()
            .productId(product.getId())
            .previousPrice(oldPrice)
            .newPrice(product.getPrice().getSalePrice())
            .changedAt(LocalDateTime.now())
            .build();
}
```

---

## 5. ProductEventPublisher 인터페이스

```java
public interface ProductEventPublisher {
    void publishProductCreated(ProductCreatedEvent event);
    void publishProductUpdated(ProductUpdatedEvent event);
    void publishProductDeleted(ProductDeletedEvent event);
    void publishProductStatusChanged(ProductStatusChangedEvent event);
    void publishProductPriceChanged(ProductPriceChangedEvent event);
}
```

---

## 6. ProductEventPublisherImpl 구현체

```java
@Slf4j
@Component
@RequiredArgsConstructor
public class ProductEventPublisherImpl implements ProductEventPublisher {

    @Override
    public void publishProductCreated(ProductCreatedEvent event) {
        log.info("Publishing ProductCreatedEvent: {}", event);
        // kafkaTemplate.send("product.created", event);
    }

    @Override
    public void publishProductUpdated(ProductUpdatedEvent event) {
        log.info("Publishing ProductUpdatedEvent: {}", event);
    }

    // 이하 동일 구조...
}
```

---

## 7. 서비스 내 이벤트 발행 흐름 예시

```java
ProductUpdatedEvent event = ProductEventMapper.toUpdatedEvent(product);
eventPublisher.publishProductUpdated(event);

if (statusChanged) {
    eventPublisher.publishProductStatusChanged(...);
}
if (priceChanged) {
    eventPublisher.publishProductPriceChanged(...);
}
```

* 상태 변경 및 가격 변경 여부는 서비스 레이어에서 이전 값과 비교하여 감지
