package com.example.catering.food;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/admin/foods")
public class AdminFoodController {
    public record FoodRequest(@NotBlank @Size(max = 120) String name, @NotBlank @Size(max = 600) String description,
        @NotNull @DecimalMin("0.01") BigDecimal price, @NotBlank @Size(max = 80) String category,
        boolean vegetarian, @Size(max = 500) String image, String tag, boolean available) {}
    private final FoodRepository foods;
    public AdminFoodController(FoodRepository foods) { this.foods = foods; }
    @GetMapping public List<FoodItem> list() { return foods.findAll(); }
    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public FoodItem create(@Valid @RequestBody FoodRequest request) {
        FoodItem food = new FoodItem(request.name(), request.description(), request.price(), request.category(), request.vegetarian(), imageValue(request.image()), request.tag());
        food.update(request.name(), request.description(), request.price(), request.category(), request.vegetarian(), imageValue(request.image()), request.tag(), request.available());
        return foods.save(food);
    }
    @PutMapping("/{id}")
    public FoodItem update(@PathVariable Long id, @Valid @RequestBody FoodRequest request) {
        FoodItem food = foods.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Food item not found"));
        food.update(request.name(), request.description(), request.price(), request.category(), request.vegetarian(), imageValue(request.image()), request.tag(), request.available());
        return foods.save(food);
    }
    private String imageValue(String image) { return image == null ? "" : image.trim(); }

    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        if (!foods.existsById(id)) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Food item not found");
        try { foods.deleteById(id); } catch (org.springframework.dao.DataIntegrityViolationException exception) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "This food item is part of existing orders; disable it instead");
        }
    }
}
