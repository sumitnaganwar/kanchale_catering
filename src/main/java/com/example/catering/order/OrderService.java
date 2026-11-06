package com.example.catering.order;

import com.example.catering.food.FoodItem;
import com.example.catering.food.FoodRepository;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class OrderService {
    private final OrderRepository orders;
    private final FoodRepository foods;
    public OrderService(OrderRepository orders, FoodRepository foods) { this.orders = orders; this.foods = foods; }

    @Transactional
    public OrderResponse place(OrderRequest request) {
        if ("Catering Event".equalsIgnoreCase(request.orderType()) &&
            (request.eventType() == null || request.eventDate() == null || request.eventLocation() == null || request.guests() == null || request.packageName() == null)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Catering event details are required");
        }
        var ids = request.items().stream().map(OrderRequest.Item::foodItemId).distinct().toList();
        Map<Long, FoodItem> byId = foods.findAllById(ids).stream().collect(Collectors.toMap(FoodItem::getId, Function.identity()));
        if (byId.size() != ids.size()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "One or more menu items no longer exist");
        BigDecimal total = BigDecimal.ZERO;
        var lines = new ArrayList<OrderLine>();
        for (var requested : request.items()) {
            FoodItem food = byId.get(requested.foodItemId());
            if (!food.isAvailable()) throw new ResponseStatusException(HttpStatus.CONFLICT, food.getName() + " is currently unavailable");
            total = total.add(food.getPrice().multiply(BigDecimal.valueOf(requested.quantity())));
            lines.add(new OrderLine(food, requested.quantity()));
        }
        var order = new CustomerOrder(request.fullName().trim(), request.phone().trim(), request.email(), request.address().trim(),
            request.city().trim(), request.orderDate(), request.deliveryTime(), request.instructions(), request.orderType(),
            request.eventType(), request.eventDate(), request.eventLocation(), request.guests(), request.packageName(), total);
        lines.forEach(order::addItem);
        return OrderResponse.from(orders.save(order));
    }

    @Transactional(readOnly = true)
    public OrderResponse track(String orderId, String phone) {
        return orders.findByOrderIdAndPhone(orderId, phone).map(OrderResponse::from)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found. Check the Order ID and mobile number."));
    }
}
