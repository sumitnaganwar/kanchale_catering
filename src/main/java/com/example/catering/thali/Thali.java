package com.example.catering.thali;

import com.example.catering.food.FoodItem;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "thalis")
public class Thali {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 120)
    private String name;
    @Column(nullable = false, length = 800)
    private String description;
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal pricePerPlate;
    @Column(nullable = false, length = 500)
    private String image;
    @Column(nullable = false)
    private int minimumPlates = 200;
    @Column(nullable = false)
    private boolean available = true;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "thali_food_items",
        joinColumns = @JoinColumn(name = "thali_id"),
        inverseJoinColumns = @JoinColumn(name = "food_item_id"))
    @OrderBy("name ASC")
    private List<FoodItem> foods = new ArrayList<>();
    @Column(nullable = false, updatable = false)
    private Instant createdAt;
    @Column(nullable = false)
    private Instant updatedAt;

    protected Thali() {}
    public Thali(String name, String description, BigDecimal pricePerPlate, String image, List<FoodItem> foods, boolean available) {
        update(name, description, pricePerPlate, image, foods, available);
    }
    public void update(String name, String description, BigDecimal pricePerPlate, String image, List<FoodItem> foods, boolean available) {
        this.name = name.trim(); this.description = description.trim(); this.pricePerPlate = pricePerPlate;
        this.image = image.trim(); this.foods.clear(); this.foods.addAll(foods); this.available = available;
    }
    @PrePersist void beforeInsert() { Instant now = Instant.now(); createdAt = now; updatedAt = now; }
    @PreUpdate void beforeUpdate() { updatedAt = Instant.now(); }
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public BigDecimal getPricePerPlate() { return pricePerPlate; }
    public String getImage() { return image; }
    public int getMinimumPlates() { return minimumPlates; }
    public boolean isAvailable() { return available; }
    public List<FoodItem> getFoods() { return foods; }
    public Instant getCreatedAt() { return createdAt; }
}
