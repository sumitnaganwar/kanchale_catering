package com.example.catering.cateringbooking;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record CateringBookingResponse(Long id, String customerName, String mobileNumber, String emailAddress,
    String eventType, LocalDate eventDate, String eventLocation, int numberOfPlates, String foodPreference,
    String cateringPackage, BigDecimal budgetPerPlate, String additionalRequirements, BookingStatus status,
    BigDecimal quotationAmount, Instant createdAt, Instant updatedAt) {
    static CateringBookingResponse from(CateringBooking booking) {
        return new CateringBookingResponse(booking.getId(), booking.getCustomerName(), booking.getMobileNumber(),
            booking.getEmailAddress(), booking.getEventType(), booking.getEventDate(), booking.getEventLocation(),
            booking.getNumberOfPlates(), booking.getFoodPreference(), booking.getCateringPackage(),
            booking.getBudgetPerPlate(), booking.getAdditionalRequirements(), booking.getStatus(),
            booking.getQuotationAmount(), booking.getCreatedAt(), booking.getUpdatedAt());
    }
}
