package com.splitmart.persistence.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "product_options")
@Getter
@Setter
@NoArgsConstructor
public class ProductOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "option_group_id")
    private ProductOptionGroup optionGroup;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "additional_price")
    private BigDecimal additionalPrice;

    @Column(length = 100)
    private String sku;

    private Integer stock;

    @Column(name = "display_order")
    private Integer displayOrder;

    @OneToMany(mappedBy = "option")
    private List<ProductImage> images = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        if (additionalPrice == null) {
            additionalPrice = BigDecimal.ZERO;
        }
        if (stock == null) {
            stock = 0;
        }
        if (displayOrder == null) {
            displayOrder = 0;
        }
    }

    /**
     * constructors
     */
    @Builder
    public ProductOption(Long id, ProductOptionGroup optionGroup, String name, BigDecimal additionalPrice, String sku, Integer stock, Integer displayOrder, List<ProductImage> images) {
        this.id = id;
        this.optionGroup = optionGroup;
        this.name = name;
        this.additionalPrice = additionalPrice;
        this.sku = sku;
        this.stock = stock;
        this.displayOrder = displayOrder;
        this.images = images;
    }
}
