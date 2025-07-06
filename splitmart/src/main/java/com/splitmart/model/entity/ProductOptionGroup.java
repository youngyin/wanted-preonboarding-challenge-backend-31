package com.splitmart.model.entity;

import com.splitmart.command.OptionGroupCommand;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "product_option_groups")
@Getter
@Setter
@NoArgsConstructor
public class ProductOptionGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "display_order")
    private Integer displayOrder;

    @OneToMany(mappedBy = "optionGroup", cascade = CascadeType.ALL)
    private List<ProductOption> options = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        if (displayOrder == null) {
            displayOrder = 0;
        }
    }

    public static ProductOptionGroup of(OptionGroupCommand optionGroup, Product product) {
        return ProductOptionGroup.builder()
                .product(product)
                .name(optionGroup.getName())
                .displayOrder(0)
                .build();
    }

    /**
     * constructors
     */
    @Builder
    public ProductOptionGroup(Long id, Product product, String name, Integer displayOrder, List<ProductOption> options) {
        this.id = id;
        this.product = product;
        this.name = name;
        this.displayOrder = displayOrder;
        this.options = options;
    }
}
