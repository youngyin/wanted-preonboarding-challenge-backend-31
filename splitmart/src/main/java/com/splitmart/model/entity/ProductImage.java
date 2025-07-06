package com.splitmart.model.entity;

import com.splitmart.command.ProductImageCommand;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "product_images")
@Getter
@Setter
@NoArgsConstructor
public class ProductImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(nullable = false)
    private String url;

    @Column(name = "alt_text")
    private String altText;

    @Column(name = "is_primary")
    private Boolean isPrimary;

    @Column(name = "display_order")
    private Integer displayOrder;

    @ManyToOne
    @JoinColumn(name = "option_id")
    private ProductOption option;

    @PrePersist
    protected void onCreate() {
        if (isPrimary == null) {
            isPrimary = false;
        }
        if (displayOrder == null) {
            displayOrder = 0;
        }
    }
    /**
     * constructors
     */
    @Builder
    public ProductImage(Long id, Product product, String url, String altText, Boolean isPrimary, Integer displayOrder, ProductOption option) {
        this.id = id;
        this.product = product;
        this.url = url;
        this.altText = altText;
        this.isPrimary = isPrimary;
        this.displayOrder = displayOrder;
        this.option = option;
    }

    /**
     * factory method
     * @return
     */
    public static ProductImage of(ProductImageCommand imageCommand, Product product) {
        return ProductImage.builder()
                .product(product)
                .url(imageCommand.getUrl())
                .altText(imageCommand.getAltText())
                .isPrimary(imageCommand.getIsPrimary())
                .displayOrder(imageCommand.getDisplayOrder())
                .build();
    }
}
