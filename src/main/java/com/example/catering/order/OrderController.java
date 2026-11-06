package com.example.catering.order;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService service;
    public OrderController(OrderService service) { this.service = service; }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse place(@Valid @RequestBody OrderRequest request) { return service.place(request); }

    @GetMapping("/track")
    public OrderResponse track(@RequestParam String orderId, @RequestParam String phone) { return service.track(orderId, phone); }
}
