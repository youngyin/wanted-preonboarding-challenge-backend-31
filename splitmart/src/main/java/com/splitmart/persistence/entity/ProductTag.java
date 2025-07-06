package com.splitmart.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "product_tags")
@Getter
@Setter
@NoArgsConstructor
public class ProductTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "tag_id")
    private Tag tag;

    // Constructors
    @Builder
    public ProductTag(Long id, Product product, Tag tag) {
        this.id = id;
        this.product = product;
        this.tag = tag;
    }

    // factory methods
    public static ProductTag of(Tag tag, Product product) {
        return ProductTag.builder()
                .product(product)
                .tag(tag)
                .build();
    }
}
