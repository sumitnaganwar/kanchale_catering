package com.example.catering.cateringbooking;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

public record CateringBookingRequest(
    @NotBlank @Size(max = 120) String customerName,
    @NotBlank @Pattern(regexp = "[+]?[0-9\\s-]{10,15}") String mobileNumber,
    @NotBlank @Email @Size(max = 180) String emailAddress,
    @NotBlank @Size(max = 80) String eventType,
    @NotNull @Future LocalDate eventDate,
    @NotBlank @Size(max = 400) String eventLocation,
    @Min(200) int numberOfPlates,
    @NotBlank String foodPreference,
    @NotBlank @Size(max = 120) String cateringPackage,
    @NotNull @DecimalMin("0.01") BigDecimal budgetPerPlate,
    @Size(max = 2000) String additionalRequirements
) {}
