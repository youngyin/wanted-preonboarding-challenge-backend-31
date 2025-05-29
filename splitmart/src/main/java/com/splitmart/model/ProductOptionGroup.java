package com.splitmart.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "product_option_groups")
@Getter
@Setter
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

    // Constructors
    public ProductOptionGroup() {
    }
}
