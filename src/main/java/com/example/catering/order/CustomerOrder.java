package com.example.catering.order;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
public class CustomerOrder {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true, length = 24)
    private String orderId;
    @Column(nullable = false, length = 120)
    private String fullName;
    @Column(nullable = false, length = 20)
    private String phone;
    private String email;
    @Column(nullable = false, length = 400)
    private String address;
    @Column(nullable = false, length = 120)
    private String city;
    @Column(nullable = false)
    private LocalDate orderDate;
    @Column(nullable = false, length = 80)
    private String deliveryTime;
    @Column(length = 1000)
    private String instructions;
    @Column(nullable = false, length = 40)
    private String orderType;
    private String eventType;
    private LocalDate eventDate;
    private String eventLocation;
    private Integer guests;
    private String cateringPackage;
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;
    @Column(nullable = false, length = 30)
    private String status = "Pending";
    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderLine> items = new ArrayList<>();

    protected CustomerOrder() {}
    public CustomerOrder(String fullName, String phone, String email, String address, String city, LocalDate orderDate,
                         String deliveryTime, String instructions, String orderType, String eventType, LocalDate eventDate,
                         String eventLocation, Integer guests, String cateringPackage, BigDecimal totalAmount) {
        this.orderId = "KC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.fullName = fullName; this.phone = phone; this.email = email; this.address = address; this.city = city;
        this.orderDate = orderDate; this.deliveryTime = deliveryTime; this.instructions = instructions;
        this.orderType = orderType; this.eventType = eventType; this.eventDate = eventDate;
        this.eventLocation = eventLocation; this.guests = guests; this.cateringPackage = cateringPackage;
        this.totalAmount = totalAmount;
    }
    public void addItem(OrderLine line) { items.add(line); line.setOrder(this); }
    public Long getId() { return id; }
    public String getOrderId() { return orderId; }
    public String getFullName() { return fullName; }
    public String getPhone() { return phone; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Instant getCreatedAt() { return createdAt; }
    public String getAddress() { return address; }
    public String getCity() { return city; }
    public LocalDate getOrderDate() { return orderDate; }
    public String getDeliveryTime() { return deliveryTime; }
    public String getOrderType() { return orderType; }
    public LocalDate getEventDate() { return eventDate; }
    public Integer getGuests() { return guests; }
    public String getEmail() { return email; }
    public List<OrderLine> getItems() { return items; }
}
