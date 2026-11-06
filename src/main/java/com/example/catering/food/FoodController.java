package com.example.catering.food;

import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class FoodController {
    private final FoodRepository foods;
    public FoodController(FoodRepository foods) { this.foods = foods; }

    @GetMapping("/foods")
    public List<FoodItem> foods() { return foods.findAllByAvailableTrueOrderByIdAsc(); }

    @GetMapping("/categories")
    public Map<String, Object> categories() {
        TreeSet<String> names = new TreeSet<>();
        foods.findAllByAvailableTrueOrderByIdAsc().forEach(item -> names.add(item.getCategory()));
        return Map.of("categories", names);
    }
}
