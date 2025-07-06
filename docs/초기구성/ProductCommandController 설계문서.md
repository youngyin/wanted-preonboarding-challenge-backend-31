
# 📘 `ProductCommandController` 설계 문서 (상품 등록/수정/삭제 API)

본 문서는 `splitmart` 프로젝트에서 상품에 대한 쓰기(Write) 작업을 처리하는 Command 전용 컨트롤러인 `ProductCommandController`의 책임과 API 명세를 기술한다.
해당 컨트롤러는 사용자 요청을 Command 객체로 매핑하여, `ProductCommandService`에 전달하고 실행 결과를 응답한다.

---

## ✅ 1. 클래스 개요

| 항목        | 설명                                                             |
| --------- | -------------------------------------------------------------- |
| 클래스명      | `ProductCommandController`                                     |
| 책임        | 상품 등록, 수정, 삭제 API 처리 (쓰기 전용)                                   |
| 경로 Prefix | `/api/products`                                                |
| 입력 형식     | JSON (Command 객체)                                              |
| 출력 형식     | JSON or `201 CREATED`, `204 NO_CONTENT` 등 REST 규약에 따른 상태 코드 반환 |

---

## ✅ 2. API 명세

### 2.1 상품 등록 – `POST /api/products`

* **요청 본문**: `CreateProductCommand`
* **요청 예시**:

```json
{
  "name": "럭셔리 소파",
  "slug": "luxury-sofa",
  "shortDescription": "고급 소파",
  "fullDescription": "<p>이 소파는...</p>",
  "sellerId": 1,
  "brandId": 2,
  "basePrice": 120000,
  "salePrice": 99000,
  "categoryIds": [10],
  "tagIds": [1, 81],
  "productDetail": {
    "weight": 10.5,
    "materials": "가죽, 금속",
    "countryOfOrigin": "대한민국"
  },
  "optionGroups": [
    {
      "name": "색상",
      "options": [
        { "name": "블랙", "additionalPrice": 0 },
        { "name": "화이트", "additionalPrice": 10000 }
      ]
    }
  ],
  "images": [
    { "url": "https://example.com/image1.jpg", "isPrimary": true }
  ]
}
```

* **응답 예시**:

```json
{
  "productId": 1234
}
```

* **HTTP 상태 코드**:

    * `201 Created` – 등록 성공
    * `400 Bad Request` – 유효성 오류
    * `409 Conflict` – 슬러그 중복

---

### 2.2 상품 수정 – `PUT /api/products/{productId}`

* **요청 본문**: `UpdateProductCommand`
* **경로 변수**: `productId`
* **설명**: 전체 상품 정보를 덮어쓰기 방식으로 수정한다.
* **상태 코드**:

    * `200 OK` – 수정 완료
    * `404 Not Found` – 존재하지 않는 상품

---

### 2.3 상품 삭제 – `DELETE /api/products/{productId}`

* **경로 변수**: `productId`
* **요청 본문**: 없음 (또는 `DeleteProductCommand` – 선택적)
* **설명**: 상품을 비활성화하거나 논리 삭제 처리
* **상태 코드**:

    * `204 No Content` – 삭제 완료
    * `404 Not Found` – 대상 없음

---

## ✅ 3. 예외 및 에러 처리

| 상황        | 상태 코드 | 메시지 예시           |
| --------- | ----- | ---------------- |
| 필수 필드 누락  | `400` | "상품명은 필수입니다"     |
| 잘못된 ID 참조 | `404` | "판매자를 찾을 수 없습니다" |
| 슬러그 중복    | `409` | "이미 존재하는 슬러그입니다" |

---

## ✅ 4. Swagger 문서화 (권장)

* `@RestController`, `@RequestMapping("/api/products")`
* 각 메서드에 `@Operation(summary = "...")`으로 Swagger 문서화 지원
* `@Parameter(description = "...")`로 입력 설명 추가 가능

---

## ✅ 5. 예시 구조

```java
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductCommandController {

    private final ProductCommandService productCommandService;

    @PostMapping
    public ResponseEntity<?> create(@RequestBody CreateProductCommand command) {
        Long id = productCommandService.createProduct(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("productId", id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody UpdateProductCommand command) {
        productCommandService.updateProduct(id, command);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        productCommandService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
```
