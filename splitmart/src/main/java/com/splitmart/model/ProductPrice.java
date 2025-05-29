package com.splitmart.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Entity
@Table(name = "product_prices")
@Getter
@Setter
public class ProductPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "base_price", nullable = false)
    private BigDecimal basePrice;

    @Column(name = "sale_price")
    private BigDecimal salePrice;

    @Column(name = "cost_price")
    private BigDecimal costPrice;

    @Column(length = 3)
    private String currency;

    @Column(name = "tax_rate")
    private BigDecimal taxRate;

    // Constructors
    public ProductPrice() {
    }
}
