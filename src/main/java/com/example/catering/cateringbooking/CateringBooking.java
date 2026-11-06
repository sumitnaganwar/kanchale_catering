package com.example.catering.cateringbooking;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "catering_bookings")
public class CateringBooking {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 120)
    private String customerName;
    @Column(nullable = false, length = 20)
    private String mobileNumber;
    @Column(nullable = false, length = 180)
    private String emailAddress;
    @Column(nullable = false, length = 80)
    private String eventType;
    @Column(nullable = false)
    private LocalDate eventDate;
    @Column(nullable = false, length = 400)
    private String eventLocation;
    @Column(nullable = false)
    private int numberOfPlates;
    @Column(nullable = false, length = 20)
    private String foodPreference;
    @Column(nullable = false, length = 120)
    private String cateringPackage;
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal budgetPerPlate;
    @Column(length = 2000)
    private String additionalRequirements;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 24)
    private BookingStatus status = BookingStatus.PENDING;
    @Column(precision = 12, scale = 2)
    private BigDecimal quotationAmount;
    @Column(nullable = false, updatable = false)
    private Instant createdAt;
    @Column(nullable = false)
    private Instant updatedAt;

    protected CateringBooking() {}

    public CateringBooking(CateringBookingRequest request) {
        this.customerName = request.customerName().trim();
        this.mobileNumber = request.mobileNumber().trim();
        this.emailAddress = request.emailAddress().trim();
        this.eventType = request.eventType().trim();
        this.eventDate = request.eventDate();
        this.eventLocation = request.eventLocation().trim();
        this.numberOfPlates = request.numberOfPlates();
        this.foodPreference = request.foodPreference().trim();
        this.cateringPackage = request.cateringPackage().trim();
        this.budgetPerPlate = request.budgetPerPlate();
        this.additionalRequirements = request.additionalRequirements();
    }

    @PrePersist
    void beforeInsert() {
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
    }
    @jakarta.persistence.PreUpdate
    void beforeUpdate() { updatedAt = Instant.now(); }
    public void updateStatus(BookingStatus status) { this.status = status; }
    public void addQuotation(BigDecimal amount) { this.quotationAmount = amount; this.status = BookingStatus.QUOTATION_SENT; }
    public Long getId() { return id; }
    public String getCustomerName() { return customerName; }
    public String getMobileNumber() { return mobileNumber; }
    public String getEmailAddress() { return emailAddress; }
    public String getEventType() { return eventType; }
    public LocalDate getEventDate() { return eventDate; }
    public String getEventLocation() { return eventLocation; }
    public int getNumberOfPlates() { return numberOfPlates; }
    public String getFoodPreference() { return foodPreference; }
    public String getCateringPackage() { return cateringPackage; }
    public BigDecimal getBudgetPerPlate() { return budgetPerPlate; }
    public String getAdditionalRequirements() { return additionalRequirements; }
    public BookingStatus getStatus() { return status; }
    public BigDecimal getQuotationAmount() { return quotationAmount; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
