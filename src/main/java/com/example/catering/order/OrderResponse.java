package com.example.catering.order;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record OrderResponse(String orderId, String status, BigDecimal totalAmount, Instant createdAt, List<Item> items) {
    public record Item(Long foodItemId, String name, int quantity, BigDecimal unitPrice) {}
    static OrderResponse from(CustomerOrder order) {
        return new OrderResponse(order.getOrderId(), order.getStatus(), order.getTotalAmount(), order.getCreatedAt(),
            order.getItems().stream().map(item -> new Item(item.getFoodItem().getId(), item.getFoodItem().getName(), item.getQuantity(), item.getUnitPrice())).toList());
    }
}
