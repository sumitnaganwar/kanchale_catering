package com.example.catering.order;

import com.example.catering.food.FoodItem;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
public class OrderLine {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "food_item_id", nullable = false)
    private FoodItem foodItem;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private CustomerOrder order;
    @Column(nullable = false)
    private int quantity;
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;
    protected OrderLine() {}
    public OrderLine(FoodItem foodItem, int quantity) { this.foodItem = foodItem; this.quantity = quantity; this.unitPrice = foodItem.getPrice(); }
    void setOrder(CustomerOrder order) { this.order = order; }
    public Long getId() { return id; }
    public FoodItem getFoodItem() { return foodItem; }
    public int getQuantity() { return quantity; }
    public BigDecimal getUnitPrice() { return unitPrice; }
}
