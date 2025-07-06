package com.splitmart.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "product_categories")
@Getter
@Setter
@NoArgsConstructor
public class ProductCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(name = "is_primary")
    private Boolean isPrimary;

    // Constructors
    @Builder
    public ProductCategory(Long id, Product product, Category category, Boolean isPrimary) {
        this.id = id;
        this.product = product;
        this.category = category;
        this.isPrimary = isPrimary;
    }

    // factory methods
    public static ProductCategory of(Category category, Product product) {
        return ProductCategory.builder()
                .product(product)
                .category(category)
                .isPrimary(false)
                .build();
    }
}
