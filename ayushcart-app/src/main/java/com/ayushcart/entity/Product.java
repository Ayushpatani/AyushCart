package com.ayushcart.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(length = 2000)
    private String description;

    /** BigDecimal, never double, for money: doubles cannot represent 0.10 exactly. */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private int stock;

    @Column(length = 500)
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    /** Soft delete: inactive products disappear from the shop but old orders still reference them. */
    @Column(nullable = false)
    private boolean active = true;

    /**
     * Optimistic locking. If two customers buy the last unit at the same moment,
     * the second transaction fails instead of pushing stock below zero.
     */
    @Version
    private Long version;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    protected Product() {
    }

    public Product(String name, String description, BigDecimal price, int stock, String imageUrl, Category category) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.imageUrl = imageUrl;
        this.category = category;
    }

    @PrePersist
    void onCreate() {
        createdAt = Instant.now();
    }

    public void decreaseStock(int quantity) {
        if (quantity > stock) {
            throw new IllegalStateException("Not enough stock for " + name);
        }
        stock -= quantity;
    }

    public void increaseStock(int quantity) {
        stock += quantity;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public Long getVersion() { return version; }
    public Instant getCreatedAt() { return createdAt; }
}
