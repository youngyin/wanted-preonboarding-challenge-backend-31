package com.splitmart.persistence.entity;

import com.splitmart.application.command.CreateProductCommand;
import com.splitmart.application.command.UpdateProductCommand;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;

@Entity
@Table(name = "product_prices")
@Getter
@Setter
@NoArgsConstructor
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

    public static ProductPrice of(CreateProductCommand command, Product product) {
        return ProductPrice.builder()
                .product(product)
                .basePrice(command.getBasePrice())
                .salePrice(command.getSalePrice())
                .currency("KRW")
                .build();
    }

    public static ProductPrice of(UpdateProductCommand command, Product product) {
        return ProductPrice.builder()
                .product(product)
                .basePrice(command.getBasePrice())
                .salePrice(command.getSalePrice())
                .currency("KRW")
                .build();
    }

    @PrePersist
    protected void onCreate() {
        if (currency == null) {
            currency = "KRW";
        }
    }

    public void update(ProductPrice price) {
        this.basePrice = price.getBasePrice();
        this.salePrice = price.getSalePrice();
        this.costPrice = price.getCostPrice();
        this.currency = price.getCurrency();
        this.taxRate = price.getTaxRate();
    }

    /**
     * constructors
     */
    @Builder
    public ProductPrice(Long id, Product product, BigDecimal basePrice, BigDecimal salePrice, BigDecimal costPrice, String currency, BigDecimal taxRate) {
        this.id = id;
        this.product = product;
        this.basePrice = basePrice;
        this.salePrice = salePrice;
        this.costPrice = costPrice;
        this.currency = currency;
        this.taxRate = taxRate;
    }
}
