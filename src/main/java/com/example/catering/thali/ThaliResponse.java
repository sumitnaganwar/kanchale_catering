package com.example.catering.thali;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record ThaliResponse(Long id, String name, String description, BigDecimal pricePerPlate, String image,
    int minimumPlates, boolean available, List<Food> foods, Instant createdAt) {
    public record Food(Long id, String name, String description, BigDecimal price, boolean vegetarian) {}
    static ThaliResponse from(Thali thali) {
        return new ThaliResponse(thali.getId(), thali.getName(), thali.getDescription(), thali.getPricePerPlate(),
            thali.getImage(), thali.getMinimumPlates(), thali.isAvailable(),
            thali.getFoods().stream().map(food -> new Food(food.getId(), food.getName(), food.getDescription(), food.getPrice(), food.isVegetarian())).toList(),
            thali.getCreatedAt());
    }
}
