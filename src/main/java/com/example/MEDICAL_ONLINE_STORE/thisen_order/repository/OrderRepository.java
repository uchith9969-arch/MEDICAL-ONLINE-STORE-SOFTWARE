package com.example.MEDICAL_ONLINE_STORE.thisen_order.repository;

import com.example.MEDICAL_ONLINE_STORE.thisen_order.model.Order;
import com.example.MEDICAL_ONLINE_STORE.thisen_order.model.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserId(Long userId);

    List<Order> findByStatus(OrderStatus status);

    List<Order> findByOrderDateBetween(LocalDateTime start, LocalDateTime end);


}
