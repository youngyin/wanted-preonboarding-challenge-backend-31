package com.splitmart.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tags")
@Getter
@Setter
@NoArgsConstructor
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 100, unique = true)
    private String slug;

    private String status;

    @OneToMany(mappedBy = "tag")
    private List<ProductTag> productTags = new ArrayList<>();

    // Constructors
    @Builder
    public Tag(Long id, String name, String slug, String status, List<ProductTag> productTags) {
        this.id = id;
        this.name = name;
        this.slug = slug;
        this.status = status;
        this.productTags = productTags;
    }
}
