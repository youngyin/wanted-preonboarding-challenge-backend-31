## Outbox 패턴 기반 이벤트 발행 설계

###  개요

이 설계는 Command 처리와 동시에 도메인 이벤트를 `outbox_events` 테이블에 저장하고, 별도 Worker가 이를 Kafka로 전송하는 Outbox 패턴 구조를 설명한다. 이 방식은 트랜잭션 일관성을 보장하며, 메시지 손실 없는 이벤트 발행을 가능하게 한다.

###  사용 기술 스택

* PostgreSQL (outbox 테이블)
* Kafka
* Spring Scheduler / KafkaTemplate
* Jackson (payload 직렬화)

---

###  아키텍처 구성도

```
[ProductCommandService]
   ├─ 도메인 트랜잭션 처리
   ├─ ProductEventPublisher (OutboxProductEventPublisher)
   ├─ outbox_events 테이블에 이벤트 저장
   
[OutboxPollerWorker]
   ├─ PENDING 상태 이벤트 조회
   ├─ Kafka로 전송
   └─ 상태 SENT / FAILED 갱신
```

---

###  ProductEventPublisher 구현체

```java
@RequiredArgsConstructor
public class OutboxProductEventPublisher implements ProductEventPublisher {
    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    private void saveOutboxEvent(String type, String id, Object event) {
        String payload = objectMapper.writeValueAsString(event);
        OutboxEvent outboxEvent = new OutboxEvent(UUID.randomUUID(),
            "Product", id, type, payload, "PENDING", LocalDateTime.now());
        outboxEventRepository.save(outboxEvent);
    }

    public void publishProductCreated(ProductCreatedEvent event) {
        saveOutboxEvent("ProductCreated", event.getProductId(), event);
    }
    public void publishProductUpdated(ProductUpdatedEvent event) {
        saveOutboxEvent("ProductUpdated", event.getProductId(), event);
    }
    public void publishProductDeleted(ProductDeletedEvent event) {
        saveOutboxEvent("ProductDeleted", event.getProductId(), event);
    }
    public void publishProductStatusChanged(ProductStatusChangedEvent event) {
        saveOutboxEvent("ProductStatusChanged", event.getProductId(), event);
    }
    public void publishProductPriceChanged(ProductPriceChangedEvent event) {
        saveOutboxEvent("ProductPriceChanged", event.getProductId(), event);
    }
}
```

---

###  Outbox 테이블 구조 (DDL 예시)

```sql
CREATE TABLE outbox_events (
  id UUID PRIMARY KEY,
  aggregate_type VARCHAR(100),
  aggregate_id VARCHAR(100),
  event_type VARCHAR(100),
  payload JSONB NOT NULL,
  status VARCHAR(20) DEFAULT 'PENDING',
  created_at TIMESTAMP DEFAULT NOW()
);
```

---

###  이벤트 전송 워커 (Scheduler 기반)

```java
@Scheduled(fixedDelay = 5000)
public void processOutbox() {
    List<OutboxEvent> events = outboxRepository.findAllByStatus("PENDING");
    for (OutboxEvent event : events) {
        try {
            kafkaTemplate.send("product.events", event.getPayload());
            event.markAsSent();
        } catch (Exception e) {
            event.markAsFailed();
        }
    }
    outboxRepository.saveAll(events);
}
```

---

###  Kafka 토픽 설계

| 이벤트 유형                  | Kafka Topic 이름           |
| ----------------------- | ------------------------ |
| 상품 생성 (`Created`)       | `product.created`        |
| 상품 수정 (`Updated`)       | `product.updated`        |
| 상품 삭제 (`Deleted`)       | `product.deleted`        |
| 상태 변경 (`StatusChanged`) | `product.status.changed` |
| 가격 변경 (`PriceChanged`)  | `product.price.changed`  |

Outbox의 event\_type과 payload 내부 값에 따라 topic 분기 가능

---

###  장단점 요약

| 장점                     | 단점                          |
| ---------------------- | --------------------------- |
| 트랜잭션 일관성 보장            | Polling 구조 필요 (Scheduler 등) |
| 표현력 높은 도메인 이벤트         | outbox 테이블 지속 관리 필요         |
| 이벤트 재처리 용이 (상태 기반)     | 테이블 부하 가능성 존재               |
| Kafka 외 메시지 브로커 전환도 유연 | 구성 복잡도 약간 증가                |

---

###  결론

Outbox 패턴은 이벤트 발행의 정합성과 안정성을 확보할 수 있는 강력한 구조로, 특히 CQRS 구조와 궁합이 뛰어나다. 별도 전송 워커만 잘 구성하면 안정적이며, 멱등성과 장애 대응 전략까지 포함한 운영도 가능하다. Kafka 외 RabbitMQ, Redis 등과도 연동이 가능해 유연성이 높다.
