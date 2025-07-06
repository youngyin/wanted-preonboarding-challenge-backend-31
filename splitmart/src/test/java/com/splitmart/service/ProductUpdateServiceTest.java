
package com.splitmart.service;

import com.splitmart.application.command.CreateProductCommand;
import com.splitmart.application.command.ProductDetailCommand;
import com.splitmart.application.command.UpdateProductCommand;
import com.splitmart.application.service.ProductRegisterService;
import com.splitmart.application.service.ProductUpdateService;
import com.splitmart.persistence.entity.Brand;
import com.splitmart.persistence.entity.Product;
import com.splitmart.persistence.entity.Seller;
import com.splitmart.persistence.repository.*;
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
class ProductUpdateServiceTest {

    @Autowired private ProductRegisterService productRegisterService;
    @Autowired private ProductUpdateService productUpdateService;
    @Autowired private ProductRepository productRepository;
    @Autowired private SellerRepository sellerRepository;
    @Autowired private BrandRepository brandRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private TagRepository tagRepository;

    private Seller seller;
    private Brand brand;

    @BeforeEach
    void setUp() {
        productRepository.deleteAllInBatch();
        brandRepository.deleteAllInBatch();
        sellerRepository.deleteAllInBatch();
        categoryRepository.deleteAllInBatch();
        tagRepository.deleteAllInBatch();

        seller = sellerRepository.save(Seller.builder().name("셀러").build());
        brand = brandRepository.save(Brand.builder().name("브랜드").slug("brand-slug").build());
    }

    @Test
    void updateProduct() {
        Long productId = productRegisterService.createProduct(CreateProductCommand.builder()
                .name("원래 상품")
                .slug("original-slug")
                .shortDescription("원래 설명")
                .sellerId(seller.getId())
                .brandId(brand.getId())
                .basePrice(BigDecimal.valueOf(10000))
                .build());

        UpdateProductCommand updateCommand = UpdateProductCommand.builder()
                .productId(productId)
                .name("수정된 상품")
                .slug("updated-slug")
                .shortDescription("수정 설명")
                .fullDescription("수정 상세")
                .sellerId(seller.getId())
                .brandId(brand.getId())
                .basePrice(BigDecimal.valueOf(15000))
                .salePrice(BigDecimal.valueOf(12000))
                .optionGroups(List.of())
                .productDetail(ProductDetailCommand.builder()
                        .weight(BigDecimal.valueOf(2.0))
                        .dimensions(Map.of("width", 20, "height", 20, "depth", 20))
                        .materials("수정 재질")
                        .build())
                .images(List.of())
                .build();

        productUpdateService.updateProduct(updateCommand);
        Product updated = productRepository.findById(productId).orElseThrow();

        assertThat(updated.getName()).isEqualTo("수정된 상품");
        assertThat(updated.getSlug()).isEqualTo("updated-slug");
        assertThat(updated.getShortDescription()).isEqualTo("수정 설명");
        assertThat(updated.getDetail().getWeight()).isEqualByComparingTo("2.0");
        assertThat(updated.getDetail().getDimensions()).containsEntry("width", 20);
    }
}
