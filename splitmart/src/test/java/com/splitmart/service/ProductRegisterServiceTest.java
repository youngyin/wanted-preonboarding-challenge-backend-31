package com.splitmart.service;

import com.splitmart.command.*;
import com.splitmart.model.entity.*;
import com.splitmart.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class ProductRegisterServiceTest {

    @Autowired private ProductRegisterService productRegisterService;
    @Autowired private ProductRepository productRepository;
    @Autowired private SellerRepository sellerRepository;
    @Autowired private BrandRepository brandRepository;
    @Autowired private ProductPriceRepository productPriceRepository;

    @BeforeEach
    void setUp() {
        productPriceRepository.deleteAllInBatch();
        productRepository.deleteAllInBatch();
        brandRepository.deleteAllInBatch();
        sellerRepository.deleteAllInBatch();
    }

    private Brand create_테스트_브랜드() {
        return brandRepository.save(
                Brand.builder().name("테스트 브랜드").slug("test-brand").build()
        );
    }

    private Seller create_테스트_셀러() {
        return sellerRepository.save(
                Seller.builder().name("테스트 셀러").build()
        );
    }

    @Test
    void createProduct() {
        Seller seller = create_테스트_셀러();
        Brand brand = create_테스트_브랜드();

        CreateProductCommand command = CreateProductCommand.builder()
                .name("상품명")
                .slug("product-slug")
                .shortDescription("간단 설명")
                .fullDescription("상세 설명")
                .sellerId(seller.getId())
                .brandId(brand.getId())
                .basePrice(BigDecimal.valueOf(10000))
                .salePrice(BigDecimal.valueOf(8000))
                .optionGroups(List.of(
                        OptionGroupCommand.builder()
                                .name("색상")
                                .options(List.of(
                                        OptionCommand.builder().name("빨강").additionalPrice(BigDecimal.valueOf(1000)).build(),
                                        OptionCommand.builder().name("파랑").build()
                                ))
                                .build()
                ))
                .productDetail(ProductDetailCommand.builder()
                        .weight(BigDecimal.valueOf(1.2))
                        .dimensions(Map.of("width", 10, "height", 10, "depth", 10))
                        .materials("플라스틱")
                        .build()
                )
                .images(List.of(
                        ProductImageCommand.builder()
                                .url("http://image.com/img1.jpg")
                                .altText("이미지")
                                .isPrimary(true)
                                .build()
                ))
                .build();

        Long productId = productRegisterService.createProduct(command);

        assertThat(productRepository.findById(productId)).isPresent();
        Product product = productRepository.findById(productId).orElseThrow();

        assertThat(product.getOptionGroups()).hasSize(1);
        assertThat(product.getImages()).hasSize(1);
        assertThat(product.getPrice().getSalePrice()).isEqualByComparingTo("8000");
        assertThat(product.getDetail().getDimensions())
                .isEqualTo(Map.of("width", 10, "height", 10, "depth", 10));
    }
}