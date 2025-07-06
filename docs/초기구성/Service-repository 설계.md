# `splitmart` 프로젝트 – 상품 Command 서비스 설계

본 문서는 CQRS 기반 이커머스 시스템에서 상품 등록, 수정, 삭제 기능을 담당하는 Command 서비스 구조를 정리한다.
Command 객체(`CreateProductCommand`, `UpdateProductCommand`, `DeleteProductCommand`)를 중심으로 각 서비스 계층의 책임과 트랜잭션 흐름, 예외 처리 방식을 명확히 정의한다.

## 1. 서비스 계층 구조

상품에 대한 쓰기 작업은 다음의 세 가지 서비스 클래스로 분리하여 구현한다.

| 서비스 클래스명                 | 책임          |
| ------------------------ | ----------- |
| `ProductRegisterService` | 상품 등록 처리    |
| `ProductUpdateService`   | 상품 정보 수정 처리 |
| `ProductDeleteService`   | 상품 삭제 처리    |

각 서비스는 `@Transactional` 어노테이션을 통해 단일 메서드 단위의 트랜잭션 처리를 보장하며, 모든 작업은 원자적으로 수행된다.

## 2. 상품 등록 처리 흐름

`ProductRegisterService#createProduct(CreateProductCommand)`는 다음 순서를 따른다.

1. 판매자 및 브랜드 조회

   * `SellerRepository`, `BrandRepository`를 통해 ID 기반 객체 조회 및 유효성 검증을 수행한다.

2. 상품 엔티티 생성

   * `Product.of(command, brand, seller, "ACTIVE")`를 호출하여 상품 객체를 생성한다.
   * 옵션, 이미지, 가격, 상세 정보는 모두 내부에서 함께 생성된다.

3. 상품 저장

   * `productRepository.save(product)` 호출로 상품을 저장하며, 연관 엔티티는 cascade 옵션에 따라 자동 저장된다.

4. 카테고리 및 태그 매핑

   * `categoryIds`, `tagIds`를 통해 `ProductCategory`, `ProductTag` 엔티티를 생성하고, 상품에 연관시킨다.

## 3. 상품 수정 처리 흐름

`ProductUpdateService#updateProduct(UpdateProductCommand)`는 다음과 같은 단계로 동작한다.

1. 커맨드 유효성 검증

   * `UpdateProductCommand#isValid()` 메서드로 필수 필드 누락 여부를 검증한다.

2. 상품, 판매자, 브랜드 조회

   * ID를 통해 기존 객체들을 조회하고 존재 여부를 확인한다.

3. 상품 기본 정보 및 연관 엔티티 수정

   * `Product#update(command, brand, seller)`를 호출한다.
   * 내부적으로 옵션 그룹, 이미지, 가격, 상세 정보도 함께 수정되며,

      * 가격과 상세 정보는 기존 객체의 상태를 변경하는 방식(update)
      * 옵션, 이미지, 카테고리, 태그는 기존 데이터를 제거 후 전체 교체(replace) 방식으로 처리한다.

4. 카테고리 및 태그 재설정

   * `categoryRepository`, `tagRepository`에서 ID로 엔티티를 조회한 뒤,
     `ProductCategory.of(...)`, `ProductTag.of(...)`를 통해 새 매핑을 구성하고 `replace`한다.

## 4. 상품 삭제 처리 흐름

`ProductDeleteService#deleteProduct(DeleteProductCommand)`는 다음 순서로 처리된다.

1. 커맨드 유효성 검증

   * `DeleteProductCommand#isValid()`로 필수 필드 검증

2. 상품 존재 여부 확인

   * `productRepository.existsById(id)`를 통해 상품이 존재하는지 확인

3. 상품 삭제

   * `productRepository.deleteById(id)`로 삭제
   * `orphanRemoval = true` 옵션에 따라 연관 엔티티도 함께 삭제된다

## 5. 저장소 계층 정의

모든 엔티티는 Spring Data JPA 기반의 `JpaRepository`로 저장소가 구성된다.

| 레포지토리                          | 엔티티                | 설명            |
| ------------------------------ | ------------------ | ------------- |
| `ProductRepository`            | Product            | 상품 기본 정보 저장   |
| `ProductDetailRepository`      | ProductDetail      | 상품 상세 정보 저장   |
| `ProductPriceRepository`       | ProductPrice       | 가격 정보 저장      |
| `ProductOptionGroupRepository` | ProductOptionGroup | 옵션 그룹 저장      |
| `ProductOptionRepository`      | ProductOption      | 옵션 항목 저장      |
| `ProductImageRepository`       | ProductImage       | 상품 이미지 저장     |
| `ProductCategoryRepository`    | ProductCategory    | 카테고리-상품 매핑 저장 |
| `ProductTagRepository`         | ProductTag         | 태그-상품 매핑 저장   |
| `SellerRepository`             | Seller             | 판매자 조회 및 참조   |
| `BrandRepository`              | Brand              | 브랜드 조회 및 참조   |
| `CategoryRepository`           | Category           | 카테고리 조회       |
| `TagRepository`                | Tag                | 태그 조회         |

## 6. 트랜잭션 처리

* 모든 쓰기 서비스 메서드는 `@Transactional`로 묶어 트랜잭션 단위로 수행된다.
* 예외 발생 시 전체 작업은 롤백되며, 일관성이 보장된다.
* Product 엔티티는 연관 엔티티들과 `cascade = ALL`, `orphanRemoval = true`로 설정되어 있어 자동으로 연동된다.

## 7. 예외 처리 및 유효성 검증 정책

* 각 Command 객체는 `isValid()` 메서드를 통해 내부 유효성을 검사할 수 있도록 설계한다.
* 존재하지 않는 ID를 사용하거나 무결성을 위반할 경우 `IllegalArgumentException`을 발생시킨다.
* DB 제약 조건 위반 등은 Spring의 `DataAccessException` 계층으로 포착되어 처리된다.
