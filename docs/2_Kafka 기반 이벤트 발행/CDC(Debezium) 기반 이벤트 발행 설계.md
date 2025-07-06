## CDC(Debezium) 기반 이벤트 발행 설계

### 개요

이 설계는 PostgreSQL의 변경 로그를 기반으로 Debezium을 통해 Kafka로 이벤트를 발행하는 CDC(Change Data Capture) 구조를 설명한다. Command 서비스에서는 별도의 publish 로직 없이 도메인 데이터를 DB에 저장하며, Debezium이 이를 감지해 Kafka에 전송한다.

### 사용 기술 스택

* PostgreSQL (binlog 지원 설정 필요)
* Kafka
* Debezium (Kafka Connect 기반)
* Kafka Connect UI or REST API

---

### 구조도

```
[ProductCommandService]  
     └──> DB (INSERT/UPDATE/DELETE only)  
         └──> [Debezium Connector]  
             └──> [Kafka Topic: product.created / product.updated / ...]  
                 └──> [Query Consumer / Elasticsearch / etc]
```

---

### ProductEventPublisher 구현체

CDC 기반에서는 Application 레벨에서 Kafka로 publish하지 않기 때문에 다음과 같은 NoOp 구현체를 사용한다.

```java
public class NoOpProductEventPublisher implements ProductEventPublisher {
    public void publishProductCreated(ProductCreatedEvent event) {}
    public void publishProductUpdated(ProductUpdatedEvent event) {}
    public void publishProductDeleted(ProductDeletedEvent event) {}
    public void publishProductStatusChanged(ProductStatusChangedEvent event) {}
    public void publishProductPriceChanged(ProductPriceChangedEvent event) {}
}
```

CommandService에서는 기존 인터페이스를 그대로 호출하지만, 실제 이벤트는 발생하지 않는다.

---

### Kafka 토픽 설계 (Debezium 전용)

| 이벤트 유형                  | Kafka Topic 이름           | 설명             |
| ----------------------- | ------------------------ | -------------- |
| 상품 생성 (`Created`)       | `product.created`        | 상품 등록 인덱싱 등    |
| 상품 수정 (`Updated`)       | `product.updated`        | 검색 인덱스 재반영 등   |
| 상품 삭제 (`Deleted`)       | `product.deleted`        | 삭제 이벤트 전달      |
| 상태 변경 (`StatusChanged`) | `product.status.changed` | 품절 등 상태 변화 이벤트 |
| 가격 변경 (`PriceChanged`)  | `product.price.changed`  | 가격 캐시 갱신 등     |

Debezium에서 생성되는 기본 토픽은 `dbserver1.public.products` 형식이지만, Kafka Connect의 SMT(Single Message Transform)를 사용하여 위와 같은 명명 방식으로 topic routing 가능하다.

---

### Debezium 설정 항목 (예시)

```json
{
  "name": "products-connector",
  "config": {
    "connector.class": "io.debezium.connector.postgresql.PostgresConnector",
    "database.hostname": "postgres",
    "database.port": "5432",
    "database.user": "debezium",
    "database.password": "dbz",
    "database.dbname": "ecommerce",
    "database.server.name": "dbserver1",
    "table.include.list": "public.products,public.product_details",
    "plugin.name": "pgoutput",
    "slot.name": "products_slot",
    "publication.autocreate.mode": "filtered"
  }
}
```

---

### 이벤트 메시지 형태 (Kafka Topic)

Debezium은 기본적으로 다음과 같은 JSON 포맷으로 메시지를 Kafka에 전달한다.

```json
{
  "before": null,
  "after": {
    "id": 1,
    "name": "슈퍼 편안한 소파",
    "status": "ON_SALE",
    ...
  },
  "source": { "table": "products" },
  "op": "c",
  "ts_ms": 1720000000000
}
```

* `op` 값: `c`(create), `u`(update), `d`(delete)
* `after` 값이 null이면 삭제 이벤트

Debezium 메시지는 일반적인 도메인 이벤트(`ProductCreatedEvent` 등) 구조와 다르며, Consumer 측에서 파싱 및 변환 로직이 필요하다.

---

### Consumer 측 변환 전략

Debezium 메시지는 Table Row 변경을 그대로 반영한 구조이므로, 다음과 같은 방법으로 도메인 이벤트로 변환 가능하다:

* Kafka Consumer 내부에서 `op` 값과 `after` 필드를 파싱하여 특정 이벤트 DTO 매핑
* ex) `op == 'c'`이면 `ProductCreatedEvent`로 매핑하여 Elasticsearch 인덱싱 등에 사용

---

### 장단점 요약

| 장점                        | 단점                          |
| ------------------------- | --------------------------- |
| 코드 변경 없음                  | 이벤트 표현력이 낮음 (도메인 이벤트 구조 아님) |
| 운영 중인 시스템에도 도입 가능         | Kafka 스키마 설계 제약 있음          |
| DB commit 기준으로 이벤트 일관성 확보 | Schema 변경 시 이슈 가능           |
| 재처리, 재구성 용이 (offset 기반)   | Consumer 측 이벤트 매핑 필요        |

---

### 결론

Debezium 기반 CDC는 도메인 코드를 건드리지 않고, DB의 변경 자체를 이벤트로 전환할 수 있는 강력한 방법이다. 특히 기존 Command 구조를 유지하면서도 Query 모델로의 데이터 동기화를 자연스럽게 할 수 있다. 단, 이벤트 표현력이 제한되며 Consumer 측에서 도메인 이벤트로의 해석과 변환 작업이 필요하다.
