package com.example.catering.food;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class MenuInitializer implements CommandLineRunner {
    private final FoodRepository foods;
    public MenuInitializer(FoodRepository foods) { this.foods = foods; }

    @Override
    public void run(String... args) {
        if (foods.count() == 0) foods.saveAll(List.of(
            new FoodItem("Puran Poli", "Soft, golden flatbread filled with sweet chana dal and jaggery.", new BigDecimal("120.00"), "Maharashtrian Food", true, "photo-1601050690597-df0568f70950", "House favourite"),
            new FoodItem("Kolhapuri Chicken", "A fiery, slow-cooked curry with our signature roasted masala.", new BigDecimal("320.00"), "Maharashtrian Food", false, "photo-1603894584373-5ac82b2ae398", null),
            new FoodItem("Paneer Butter Masala", "Creamy tomato gravy, fresh paneer and a touch of kasuri methi.", new BigDecimal("260.00"), "North Indian Food", true, "photo-1631452180519-c014fe946bc7", "Bestseller"),
            new FoodItem("Masala Dosa", "Crisp rice crepe with spiced potato, coconut chutney and sambar.", new BigDecimal("150.00"), "South Indian Food", true, "photo-1589301760014-d929f3979dbc", null),
            new FoodItem("Veg Hakka Noodles", "Wok-tossed noodles with crunchy vegetables and house-made sauce.", new BigDecimal("190.00"), "Chinese Food", true, "photo-1585032226651-759b368d7246", null),
            new FoodItem("Crispy Veg Platter", "A sharing platter of golden, crunchy evening favourites.", new BigDecimal("240.00"), "Snacks & Starters", true, "photo-1601050690117-94f5f6fa8bd7", "For sharing"),
            new FoodItem("Gulab Jamun", "Warm, syrup-soaked khoya dumplings with a hint of cardamom.", new BigDecimal("90.00"), "Sweets & Desserts", true, "photo-1666190092159-1f17c7c6e2c9", null),
            new FoodItem("Celebration Feast", "A generous spread for your guests, thoughtfully made from scratch.", new BigDecimal("599.00"), "Catering Packages", true, "photo-1547592180-85f173990554", "Events"),
            new FoodItem("Kanchale Special Thali", "Seasonal sabzi, dal, rice, chapati, pickle and a sweet finish.", new BigDecimal("280.00"), "Special Thali", true, "photo-1546833999-b9f581a1996d", "Complete meal")
        ));
        addIfMissing(new FoodItem("Poha", "A light Maharashtrian breakfast of flattened rice, peanuts, lemon and fresh coriander.", new BigDecimal("60.00"), "Maharashtrian Food", true, "photo-1601050690597-df0568f70950", "Breakfast favourite"));
        addIfMissing(new FoodItem("Vada Pav", "Mumbai-style spiced potato fritter tucked into a soft pav with chutneys.", new BigDecimal("50.00"), "Snacks & Starters", true, "photo-1606491956689-2ea866880c84", "Mumbai classic"));
    }

    private void addIfMissing(FoodItem item) {
        if (!foods.existsByName(item.getName())) foods.save(item);
    }
}
