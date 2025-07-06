# 📘 Command 설계 문서 (상품 등록/수정/삭제)

본 문서는 CQRS 기반 이커머스 시스템의 Command 모델 설계 원칙에 따라, \*\*쓰기 작업(Write Operations)\*\*을 수행하기 위한 명령 객체(Command Object)의 구조를 정의한다.
해당 Command 객체들은 서비스 계층에서 직접 사용되며, 컨트롤러 입력(Request)와 도메인 간의 명확한 경계를 형성한다.

---

## 1️⃣ `CreateProductCommand`

상품 등록을 위한 기본 명령 객체로, 상품의 주요 정보와 옵션, 카테고리, 태그, 가격 등을 포함한다.

### 포함 정보

| 필드명              | 타입                       | 설명                   |
| ---------------- | ------------------------ | -------------------- |
| name             | String                   | 상품명                  |
| slug             | String                   | URL에 사용할 슬러그 (중복 불가) |
| shortDescription | String                   | 짧은 설명                |
| fullDescription  | String                   | 상세 설명 (HTML 허용)      |
| sellerId         | Long                     | 판매자 ID (FK)          |
| brandId          | Long                     | 브랜드 ID (FK)          |
| basePrice        | BigDecimal               | 기본 가격                |
| salePrice        | BigDecimal               | 세일 가격 (선택)           |
| categoryIds      | List<Long>               | 연결할 카테고리 ID 목록       |
| tagIds           | List<Long>               | 연결할 태그 ID 목록         |
| optionGroups     | List<OptionGroupCommand> | 옵션 그룹과 하위 옵션         |

---

## 2️⃣ `OptionGroupCommand`

상품의 옵션 그룹(예: 색상, 사이즈)을 표현하는 하위 명령 객체이다.

| 필드명     | 타입                  | 설명                   |
| ------- | ------------------- | -------------------- |
| name    | String              | 옵션 그룹명 (ex. 색상, 사이즈) |
| options | List<OptionCommand> | 옵션 항목 목록             |

---

## 3️⃣ `OptionCommand`

실제 사용자가 선택 가능한 옵션 항목을 표현한다.

| 필드명             | 타입         | 설명              |
| --------------- | ---------- | --------------- |
| name            | String     | 옵션명 (예: 블랙, XL) |
| additionalPrice | BigDecimal | 추가 금액 (없을 경우 0) |

---

## 4️⃣ `UpdateProductCommand`

상품 수정 요청에 사용되는 명령 객체이다. `CreateProductCommand`와 필드 구조는 유사하지만, ID 기준으로 업데이트 처리를 해야 하므로, 서비스 로직에서는 `productId`를 분리해서 처리한다.

> 선택적으로 일부 필드만 업데이트하도록 설계해도 되지만, 실습에서는 **전체 덮어쓰기 방식**을 기본으로 한다.

---

## 5️⃣ `DeleteProductCommand` *(선택 가능)*

별도 Command 객체를 만들지 않고 `Long id`만 받을 수도 있지만, 이벤트 발행 구조가 있다면 Command 객체로 정의해두는 것도 좋다.

| 필드명       | 타입   | 설명        |
| --------- | ---- | --------- |
| productId | Long | 삭제할 상품 ID |

---

## ✏️ 설계 가이드

* Command 객체는 **순수 데이터 구조**로 유지한다 (setter or builder 패턴 사용)
* Validation 또는 `toEntity(...)` 같은 변환 로직은 Command 내부에서 정의할 수도 있다
* 옵션, 태그, 카테고리 등의 하위 구조는 계층적으로 설계하여 **복잡도 분리**에 유리하게 한다
* \*\*읽기 모델(Query)\*\*과는 구조적으로 분리되어야 한다 (DTO 혼용 금지)