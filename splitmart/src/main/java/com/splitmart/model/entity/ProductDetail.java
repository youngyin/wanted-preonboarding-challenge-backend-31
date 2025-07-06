package com.splitmart.model.entity;

import com.splitmart.command.ProductDetailCommand;
import com.splitmart.infrastructure.util.JsonConverter;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.util.Map;

@Entity
@Table(name = "product_details")
@Getter
@Setter
@NoArgsConstructor
public class ProductDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private BigDecimal weight;

    @JdbcTypeCode(SqlTypes.JSON)
    @Convert(converter = JsonConverter.class)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> dimensions;

    @Column(columnDefinition = "TEXT")
    private String materials;

    @Column(name = "country_of_origin")
    private String countryOfOrigin;

    @Column(name = "warranty_info", columnDefinition = "TEXT")
    private String warrantyInfo;

    @Column(name = "care_instructions", columnDefinition = "TEXT")
    private String careInstructions;

    @JdbcTypeCode(SqlTypes.JSON)
    @Convert(converter = JsonConverter.class)
    @Column(name = "additional_info")
    private Map<String, Object> additionalInfo;

    public static ProductDetail of(ProductDetailCommand productDetail, Product product) {
        if (productDetail == null) return null;
        return ProductDetail.builder()
                .product(product)
                .weight(productDetail.getWeight())
                .dimensions(productDetail.getDimensions())
                .materials(productDetail.getMaterials())
                .countryOfOrigin(productDetail.getCountryOfOrigin())
                .warrantyInfo(productDetail.getWarrantyInfo())
                .careInstructions(productDetail.getCareInstructions())
                .additionalInfo(productDetail.getAdditionalInfo())
                .build();
    }

    public void update(ProductDetail detail) {
        if (detail == null) return;

        this.weight = detail.getWeight();
        this.dimensions = detail.getDimensions();
        this.materials = detail.getMaterials();
        this.warrantyInfo = detail.getWarrantyInfo();
        this.careInstructions = detail.getCareInstructions();
        this.countryOfOrigin = detail.getCountryOfOrigin();
        this.additionalInfo = detail.getAdditionalInfo();
    }

    /**
     * construct
     */
    @Builder
    public ProductDetail(Long id, Product product, BigDecimal weight, Map<String, Object> dimensions, String materials, String countryOfOrigin, String warrantyInfo, String careInstructions, Map<String, Object> additionalInfo) {
        this.id = id;
        this.product = product;
        this.weight = weight;
        this.dimensions = dimensions;
        this.materials = materials;
        this.countryOfOrigin = countryOfOrigin;
        this.warrantyInfo = warrantyInfo;
        this.careInstructions = careInstructions;
        this.additionalInfo = additionalInfo;
    }
}
