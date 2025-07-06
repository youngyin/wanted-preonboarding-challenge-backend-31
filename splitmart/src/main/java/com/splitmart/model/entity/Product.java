package com.splitmart.model.entity;

import com.splitmart.command.CreateProductCommand;
import com.splitmart.command.UpdateProductCommand;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String slug;

    @Column(name = "short_description", length = 500)
    private String shortDescription;

    @Column(name = "full_description", columnDefinition = "TEXT")
    private String fullDescription;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "seller_id")
    private Seller seller;

    @ManyToOne
    @JoinColumn(name = "brand_id")
    private Brand brand;

    @Column(nullable = false, length = 20)
    private String status;

    
    @OneToOne(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private ProductDetail detail;

    
    @OneToOne(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private ProductPrice price;

    
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductOptionGroup> optionGroups = new ArrayList<>();

    
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductImage> images = new ArrayList<>();

    
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductCategory> categories = new ArrayList<>();

    
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductTag> tags = new ArrayList<>();

    
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Create a Product from a CreateProductCommand
     * @param command
     * @param brand
     * @param seller
     * @param status
     * @return
     */
    public static Product of(CreateProductCommand command,
                             Brand brand, Seller seller,
                             String status
    ) {
        Product product = Product.builder()
                .name(command.getName())
                .slug(command.getSlug())
                .shortDescription(command.getShortDescription())
                .fullDescription(command.getFullDescription())
                .seller(seller)
                .brand(brand)
                .status(status)
                .build();

        command.getOptionGroups().forEach(optionGroup -> product.addOptionGroup(ProductOptionGroup.of(optionGroup, product)));
        command.getImages().forEach(image -> product.addImage(ProductImage.of(image, product)));
        product.updatePrice(ProductPrice.of(command, product));
        product.replaceDetail(ProductDetail.of(command.getProductDetail(), product));

        return product;
    }

    /**
     * Update a Product from a UpdateProductCommand
     * @param command
     * @return
     **/
    public void update(UpdateProductCommand command,
                       Brand brand, Seller seller){
        this.name = command.getName();
        this.slug = command.getSlug();
        this.shortDescription = command.getShortDescription();
        this.fullDescription = command.getFullDescription();
        this.seller = seller;
        this.brand = brand;

        this.replaceOptionGroup(command.getOptionGroups().stream().map(optionGroup -> ProductOptionGroup.of(optionGroup, this)).toList());
        this.replaceImages(command.getImages().stream().map(image -> ProductImage.of(image, this)).toList());
        this.updatePrice(ProductPrice.of(command, this));
        this.replaceDetail(ProductDetail.of(command.getProductDetail(), this));
    }

    /**
     * Update a OptionGroup
     * @return
     */
    public void addOptionGroup(ProductOptionGroup optionGroup) {
        if (optionGroup == null) return;
        if (optionGroups == null) optionGroups = new ArrayList<>();
        optionGroups.add(optionGroup);
        optionGroup.setProduct(this);
    }

    public void removeOptionGroup(ProductOptionGroup optionGroup) {
        if (optionGroup == null) return;
        if (optionGroups == null) return;
        optionGroups.remove(optionGroup);
        optionGroup.setProduct(null);
    }

    public void replaceOptionGroup(List<ProductOptionGroup> optionGroups) {
        if (optionGroups == null) return;
        if (this.optionGroups == null) this.optionGroups = new ArrayList<>();
        this.optionGroups.clear();
        for (ProductOptionGroup optionGroup : optionGroups) {
            this.optionGroups.add(optionGroup);
            optionGroup.setProduct(this);
        }
    }

    /**
     * Update a Image
     * @return
     */
    public void addImage(ProductImage image) {
        if (image == null) return;
        if (images == null) images = new ArrayList<>();
        images.add(image);
        image.setProduct(this);
    }

    public void removeImage(ProductImage image) {
        if (image == null) return;
        if (images == null) return;
        images.remove(image);
        image.setProduct(null);
    }

    public void replaceImages(List<ProductImage> images) {
        if (images == null) return;
        if (this.images == null) this.images = new ArrayList<>();
        this.images.clear();
        for (ProductImage image : images) {
            this.images.add(image);
            image.setProduct(this);
        }
    }

    /**
     * Update a Price
     * @return
     */
    public void removePrice() {
        if (price == null) return;
        price.setProduct(null);
        price = null;
    }

    public void updatePrice(ProductPrice price) {
        if (price == null) return;
        if (this.price != null) {
            this.price.update(price);
            return;
        }
        this.price = price;
        price.setProduct(this);
    }

    /**
     * Update a Category
     * @return
     */
    public void addCategory(ProductCategory category) {
        if (category == null) return;
        if (categories == null) categories = new ArrayList<>();
        categories.add(category);
        category.setProduct(this);
    }

    public void removeCategory(ProductCategory category) {
        if (category == null) return;
        if (categories == null) return;
        categories.remove(category);
        category.setProduct(null);
    }

    public void replaceCategories(List<ProductCategory> categories) {
        if (categories == null) return;
        if (this.categories == null) this.categories = new ArrayList<>();
        this.categories.clear();
        for (ProductCategory category : categories) {
            this.categories.add(category);
            category.setProduct(this);
        }
    }

    /**
     * Update a Tag
     * @return
     */
    public void addTag(ProductTag tag) {
        if (tag == null) return;
        if (tags == null) tags = new ArrayList<>();
    }

    public void removeTag(ProductTag tag) {
        if (tag == null) return;
        if (tags == null) return;
        tags.remove(tag);
    }

    public void replaceTags(List<ProductTag> tags) {
        if (tags == null) return;
        if (this.tags == null) this.tags = new ArrayList<>();
        this.tags.clear();
        for (ProductTag tag : tags) {
            this.tags.add(tag);
        }
    }

    /**
     * Update a Review
     * @return
     */
    public void addReview(Review review) {
        if (review == null) return;
        if (reviews == null) reviews = new ArrayList<>();
        reviews.add(review);
        review.setProduct(this);
    }

    public void removeReview(Review review) {
        if (review == null) return;
        if (reviews == null) return;
        reviews.remove(review);
        review.setProduct(null);
    }

    public void replaceReviews(List<Review> reviews) {
        if (reviews == null) return;
        if (this.reviews == null) this.reviews = new ArrayList<>();
        this.reviews.clear();
        for (Review review : reviews) {
            this.reviews.add(review);
            review.setProduct(this);
        }
    }

    /**
     * Update a Detail
     * @return
     */
    public void removeDetail() {
        if (detail == null) return;
        detail.setProduct(null);
        detail = null;
    }

    public void replaceDetail(ProductDetail detail) {
        if (detail == null) return;
        if (this.detail != null) {
            this.detail.update(detail);
            return;
        }
        this.detail = detail;
        detail.setProduct(this);
    }

    /**
     * construct
     */
    @Builder
    public Product(Long id, String name, String slug, String shortDescription, String fullDescription, LocalDateTime createdAt, LocalDateTime updatedAt, Seller seller, Brand brand, String status, ProductDetail detail, ProductPrice price, List<ProductOptionGroup> optionGroups, List<ProductImage> images, List<ProductCategory> categories, List<ProductTag> tags, List<Review> reviews) {
        this.id = id;
        this.name = name;
        this.slug = slug;
        this.shortDescription = shortDescription;
        this.fullDescription = fullDescription;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.seller = seller;
        this.brand = brand;
        this.status = status;
        this.detail = detail;
        this.price = price;
        this.optionGroups = optionGroups;
        this.images = images;
        this.categories = categories;
        this.tags = tags;
        this.reviews = reviews;
    }
}
