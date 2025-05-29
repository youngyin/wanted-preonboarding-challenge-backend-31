package com.splitmart.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Entity
@Table(name = "product_details")
@Getter
@Setter
public class ProductDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private BigDecimal weight;

    @Column(columnDefinition = "jsonb")
    private String dimensions;

    @Column(columnDefinition = "TEXT")
    private String materials;

    @Column(name = "country_of_origin")
    private String countryOfOrigin;

    @Column(name = "warranty_info", columnDefinition = "TEXT")
    private String warrantyInfo;

    @Column(name = "care_instructions", columnDefinition = "TEXT")
    private String careInstructions;

    @Column(name = "additional_info", columnDefinition = "jsonb")
    private String additionalInfo;

    // Constructors
    public ProductDetail() {
    }
}
