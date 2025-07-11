package com.splitmart.service;

import com.splitmart.adapter.event.ProductEventPublisher;
import com.splitmart.application.command.CreateProductCommand;
import com.splitmart.application.command.DeleteProductCommand;
import com.splitmart.application.service.ProductDeleteService;
import com.splitmart.application.service.ProductRegisterService;
import com.splitmart.persistence.entity.Brand;
import com.splitmart.persistence.entity.Seller;
import com.splitmart.persistence.repository.BrandRepository;
import com.splitmart.persistence.repository.ProductRepository;
import com.splitmart.persistence.repository.SellerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class ProductDeleteServiceTest {

    @Autowired private ProductDeleteService productDeleteService;
    @Autowired private ProductRegisterService productRegisterService;
    @Autowired private ProductRepository productRepository;
    @Autowired private SellerRepository sellerRepository;
    @Autowired private BrandRepository brandRepository;
    @MockitoBean private ProductEventPublisher productEventPublisher;

    private Seller seller;
    private Brand brand;

    @BeforeEach
    void setUp() {
        productRepository.deleteAllInBatch();
        sellerRepository.deleteAllInBatch();
        brandRepository.deleteAllInBatch();

        seller = sellerRepository.save(Seller.builder().name("테스트 셀러").build());
        brand = brandRepository.save(Brand.builder().name("테스트 브랜드").slug("brand-slug").build());
    }

    @Test
    void deleteProduct_success() {
        // given
        Long productId = productRegisterService.createProduct(CreateProductCommand.builder()
                .name("삭제 테스트 상품")
                .slug("delete-test")
                .shortDescription("삭제 대상 설명")
                .sellerId(seller.getId())
                .brandId(brand.getId())
                .basePrice(BigDecimal.valueOf(10000))
                .build());

        assertThat(productRepository.existsById(productId)).isTrue();

        // when
        productDeleteService.deleteProduct(DeleteProductCommand.of(productId));

        // then
        assertThat(productRepository.existsById(productId)).isFalse();
    }

    @Test
    void deleteProduct_whenProductNotFound_thenThrows() {
        // given
        Long invalidId = 9999L;

        // then
        assertThatThrownBy(() -> productDeleteService.deleteProduct(DeleteProductCommand.of(invalidId)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Product not found");
    }
}