
# 이벤트 객체 및 Kafka 토픽 설계 문서

본 문서는 Splitmart 프로젝트 내 Command 모델에서 발행되는 **도메인 이벤트 객체 구조**와 **Kafka 토픽 설계 원칙**을 정의한다.
해당 이벤트는 Query 모델, 알림 시스템 등 다양한 Consumer에서 처리될 수 있도록 설계된다.

---

## 1. 이벤트 설계 원칙

| 항목          | 내용                                                          |
| ----------- | ----------------------------------------------------------- |
| 이벤트 이름 규칙   | `ProductCreatedEvent`, `ProductUpdatedEvent` 등 과거형 명사 기반 명명 |
| Topic 이름 규칙 | `product.created`, `product.updated` 등 도메인.이벤트 형식           |
| 전송 방식       | JSON 직렬화 (Avro 등은 미사용)                                      |
| 포함 필드       | Consumer 입장에서 필요한 정보만 최소한으로 포함                              |
| 타임스탬프 필수    | 모든 이벤트에는 `eventTime` 필드 포함하여 처리 순서 추적 가능                    |

---

## 2. Kafka 토픽 설계

| 이벤트 유형                  | 발행 시점                 | Kafka Topic 이름           | 단일 Consumer 용도 |
| ----------------------- | --------------------- | ------------------------ | -------------- |
| 상품 생성 (`Created`)       | 상품 최초 등록 성공 시         | `product.created`        | 상품 인덱싱, 알림 등   |
| 상품 수정 (`Updated`)       | 상품 이름, 설명 등 수정 시      | `product.updated`        | 검색 인덱스 재반영     |
| 상품 삭제 (`Deleted`)       | 상품 삭제 시               | `product.deleted`        | 인덱스 제거         |
| 상태 변경 (`StatusChanged`) | 상품 상태(판매중, 품절 등) 변경 시 | `product.status.changed` | 화면 숨김, 필터링     |
| 가격 변경 (`PriceChanged`)  | 판매가, 원가 등 가격 변경 시     | `product.price.changed`  | 가격 캐시 갱신       |

※ Topic 이름은 `.`, 소문자 기반이며 도메인명+이벤트명 형식으로 일관되게 구성한다.

---

## 3. 이벤트 객체 명세

### 3.1 `ProductCreatedEvent`

```json
{
  "eventType": "ProductCreatedEvent",
  "productId": 123,
  "name": "상품명",
  "slug": "product-slug",
  "sellerId": 1,
  "brandId": 2,
  "basePrice": 10000,
  "salePrice": 9000,
  "status": "ACTIVE",
  "createdAt": "2025-07-06T14:00:00"
}
```

### 3.2 `ProductUpdatedEvent`

```json
{
  "eventType": "ProductUpdatedEvent",
  "productId": 123,
  "name": "수정된 상품명",
  "slug": "updated-slug",
  "updatedAt": "2025-07-06T15:00:00"
}
```

### 3.3 `ProductDeletedEvent`

```json
{
  "eventType": "ProductDeletedEvent",
  "productId": 123,
  "deletedAt": "2025-07-06T16:00:00"
}
```

### 3.4 `ProductStatusChangedEvent`

```json
{
  "eventType": "ProductStatusChangedEvent",
  "productId": 123,
  "previousStatus": "ACTIVE",
  "newStatus": "INACTIVE",
  "reason": "품절 처리",
  "changedAt": "2025-07-06T17:00:00"
}
```

### 3.5 `ProductPriceChangedEvent`

```json
{
  "eventType": "ProductPriceChangedEvent",
  "productId": 123,
  "previousPrice": 9000,
  "newPrice": 8500,
  "changedAt": "2025-07-06T18:00:00"
}
```

---

## 4. 공통 필드 구조 (Event Superclass)

모든 이벤트는 아래 공통 필드를 포함한다:

| 필드명         | 타입              | 설명                 |
| ----------- | --------------- | ------------------ |
| `eventType` | `String`        | 이벤트 클래스명 혹은 타입 식별자 |
| `productId` | `Long`          | 상품 ID              |
| `eventTime` | `LocalDateTime` | 이벤트 발생 시각          |

※ 추상 클래스나 인터페이스(`BaseProductEvent`)를 둘 수도 있으나, 직렬화 구조 단순화를 위해 선택적 구성 가능

---

## 5. 이벤트 객체 클래스 예시

```java
@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ProductCreatedEvent {
    private String eventType = "ProductCreatedEvent";
    private Long productId;
    private String name;
    private String slug;
    private Long sellerId;
    private Long brandId;
    private BigDecimal basePrice;
    private BigDecimal salePrice;
    private String status;
    private LocalDateTime createdAt;
}
```

---

## 6. 이벤트 발행 위치

각 서비스(Command):

* `ProductRegisterService`: 생성 후 `ProductCreatedEvent` 발행
* `ProductUpdateService`: 수정 후 `ProductUpdatedEvent` 및 변경 감지 이벤트 추가 발행
* `ProductDeleteService`: 삭제 후 `ProductDeletedEvent` 발행
