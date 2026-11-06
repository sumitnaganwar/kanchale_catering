package com.example.catering.order;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<CustomerOrder, Long> {
    Optional<CustomerOrder> findByOrderIdAndPhone(String orderId, String phone);
    Optional<CustomerOrder> findByOrderId(String orderId);
}
