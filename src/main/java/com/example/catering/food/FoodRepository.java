package com.example.catering.food;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FoodRepository extends JpaRepository<FoodItem, Long> {
    List<FoodItem> findAllByAvailableTrueOrderByIdAsc();
    boolean existsByName(String name);
}
