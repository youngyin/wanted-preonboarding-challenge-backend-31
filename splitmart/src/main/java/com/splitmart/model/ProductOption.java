package com.splitmart.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "product_options")
@Getter
@Setter
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

    // Constructors
    public ProductOption() {
    }
}
