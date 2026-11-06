package com.example.catering.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.transaction.annotation.Transactional;

@RestController
@RequestMapping("/api/admin")
public class AdminOrderController {
    private static final List<String> STATUSES = List.of("Pending", "Confirmed", "Preparing", "Ready", "Out for Delivery", "Completed", "Cancelled");
    private final OrderRepository orders;
    public AdminOrderController(OrderRepository orders) { this.orders = orders; }
    public record StatusRequest(@NotBlank String status) {}
    public record OrderRow(String orderId, String customerName, String phone, String email, String address, String city,
                           String orderType, String status, BigDecimal totalAmount, java.time.Instant createdAt,
                           java.time.LocalDate orderDate, String deliveryTime, java.time.LocalDate eventDate, Integer guests,
                           List<OrderResponse.Item> items) {
        static OrderRow from(CustomerOrder order) {
            return new OrderRow(order.getOrderId(), order.getFullName(), order.getPhone(), order.getEmail(), order.getAddress(), order.getCity(),
                order.getOrderType(), order.getStatus(), order.getTotalAmount(), order.getCreatedAt(), order.getOrderDate(), order.getDeliveryTime(),
                order.getEventDate(), order.getGuests(), order.getItems().stream().map(item -> new OrderResponse.Item(item.getFoodItem().getId(), item.getFoodItem().getName(), item.getQuantity(), item.getUnitPrice())).toList());
        }
    }
    @GetMapping("/orders")
    @Transactional(readOnly = true)
    public List<OrderRow> allOrders() { return orders.findAll().stream().sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt())).map(OrderRow::from).toList(); }
    @PatchMapping("/orders/{orderId}/status")
    @Transactional
    public OrderRow updateStatus(@PathVariable String orderId, @Valid @RequestBody StatusRequest request) {
        if (!STATUSES.contains(request.status())) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid order status");
        CustomerOrder order = orders.findByOrderId(orderId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
        order.setStatus(request.status());
        return OrderRow.from(orders.save(order));
    }
    @GetMapping("/dashboard")
    @Transactional(readOnly = true)
    public Map<String, Object> dashboard() {
        var all = orders.findAll();
        long pending = all.stream().filter(o -> o.getStatus().equals("Pending")).count();
        long confirmed = all.stream().filter(o -> o.getStatus().equals("Confirmed")).count();
        long completed = all.stream().filter(o -> o.getStatus().equals("Completed")).count();
        long cancelled = all.stream().filter(o -> o.getStatus().equals("Cancelled")).count();
        BigDecimal revenue = all.stream().filter(o -> !o.getStatus().equals("Cancelled")).map(CustomerOrder::getTotalAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        return Map.of("totalOrders", all.size(), "pendingOrders", pending, "confirmedOrders", confirmed,
            "completedOrders", completed, "cancelledOrders", cancelled, "revenue", revenue,
            "recentOrders", all.stream().sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt())).limit(8).map(OrderRow::from).toList());
    }
    @GetMapping("/customers")
    @Transactional(readOnly = true)
    public List<Map<String, Object>> customers() {
        return orders.findAll().stream().collect(java.util.stream.Collectors.toMap(CustomerOrder::getPhone,
            order -> Map.<String,Object>of("name", order.getFullName(), "phone", order.getPhone(), "email", order.getEmail() == null ? "" : order.getEmail(), "orders", 1, "orderId", order.getOrderId()),
            (first, next) -> Map.of("name", first.get("name"), "phone", first.get("phone"), "email", first.get("email"), "orders", ((Integer) first.get("orders")) + 1, "orderId", first.get("orderId")))).values().stream().toList();
    }
}
