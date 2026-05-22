package com.example.MEDICAL_ONLINE_STORE.sandali_payment.repository;

import com.example.MEDICAL_ONLINE_STORE.sandali_payment.model.Payment;
import com.example.MEDICAL_ONLINE_STORE.sandali_payment.model.PaymentMethod;
import com.example.MEDICAL_ONLINE_STORE.sandali_payment.model.PaymentStatus;
import com.example.MEDICAL_ONLINE_STORE.thisen_order.model.OrderItem;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    // Get payment by order ID
    Optional<Payment> findByOrderId(Long orderId);

    List<OrderItem> findByOrder_Id(Long orderId);

    // Get all payments by status
    List<Payment> findByPaymentStatus(PaymentStatus paymentStatus);

    // Get all payments by method
    List<Payment> findByPaymentMethod(PaymentMethod paymentMethod);

    // Check if a payment exists for an order
    boolean existsByOrderId(Long orderId);

    // Get payments between two dates
    List<Payment> findByPaymentDateBetween(LocalDateTime start, LocalDateTime end);
}