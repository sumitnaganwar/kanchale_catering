package com.example.catering.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.List;

public record OrderRequest(
    @NotBlank @Size(max = 120) String fullName,
    @NotBlank @Pattern(regexp = "[+]?[0-9\\s-]{10,15}") String phone,
    @Email @Size(max = 180) String email,
    @NotBlank @Size(max = 400) String address,
    @NotBlank @Size(max = 120) String city,
    @NotNull @FutureOrPresent LocalDate orderDate,
    @NotBlank @Size(max = 80) String deliveryTime,
    @Size(max = 1000) String instructions,
    @NotBlank String orderType,
    String eventType,
    LocalDate eventDate,
    String eventLocation,
    @Positive Integer guests,
    String packageName,
    @NotEmpty List<@Valid Item> items
) {
    public record Item(@NotNull Long foodItemId, @Min(1) @Max(100) int quantity) {}
}
