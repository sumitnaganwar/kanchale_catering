package com.example.catering.food;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "food_items")
public class FoodItem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 120)
    private String name;
    @Column(nullable = false, length = 600)
    private String description;
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
    @Column(nullable = false, length = 80)
    private String category;
    @Column(nullable = false)
    private boolean vegetarian;
    @Column(nullable = false, length = 500)
    private String image;
    private String tag;
    @Column(nullable = false)
    private boolean available = true;

    protected FoodItem() {}
    public FoodItem(String name, String description, BigDecimal price, String category, boolean vegetarian, String image, String tag) {
        this.name = name; this.description = description; this.price = price; this.category = category;
        this.vegetarian = vegetarian; this.image = image; this.tag = tag; this.available = true;
    }
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public BigDecimal getPrice() { return price; }
    public String getCategory() { return category; }
    public boolean isVegetarian() { return vegetarian; }
    public String getImage() { return image; }
    public String getTag() { return tag; }
    public boolean isAvailable() { return available; }
    public void update(String name, String description, BigDecimal price, String category, boolean vegetarian, String image, String tag, boolean available) {
        this.name = name; this.description = description; this.price = price; this.category = category;
        this.vegetarian = vegetarian; this.image = image; this.tag = tag; this.available = available;
    }
}
