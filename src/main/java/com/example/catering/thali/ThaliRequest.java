package com.example.catering.thali;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;

public record ThaliRequest(
    @NotBlank @Size(max = 120) String name,
    @NotBlank @Size(max = 800) String description,
    @NotNull @DecimalMin("0.01") BigDecimal pricePerPlate,
    @NotBlank @Size(max = 500) String image,
    @NotEmpty List<@NotNull Long> foodIds,
    boolean available
) {}
