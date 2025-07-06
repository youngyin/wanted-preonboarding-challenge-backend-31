package com.splitmart.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 100, unique = true)
    private String slug;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<Category> children = new ArrayList<>();

    @Column(nullable = false)
    private Integer level;

    @Column(name = "image_url")
    private String imageUrl;

    private String status;
    private Long displayOrder;

    @OneToMany(mappedBy = "category")
    private List<ProductCategory> productCategories = new ArrayList<>();

    // Constructors

    @Builder
    public Category(List<ProductCategory> productCategories, Long displayOrder, String status, String imageUrl, Integer level, List<Category> children, Category parent, String description, String slug, String name, Long id) {
        this.productCategories = productCategories;
        this.displayOrder = displayOrder;
        this.status = status;
        this.imageUrl = imageUrl;
        this.level = level;
        this.children = children;
        this.parent = parent;
        this.description = description;
        this.slug = slug;
        this.name = name;
        this.id = id;
    }
}
