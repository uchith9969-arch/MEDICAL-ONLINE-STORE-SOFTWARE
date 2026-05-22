package com.example.MEDICAL_ONLINE_STORE.sandali_payment.dto;

import com.example.MEDICAL_ONLINE_STORE.sandali_payment.model.PaymentMethod;
import com.example.MEDICAL_ONLINE_STORE.sandali_payment.model.PaymentStatus;

import java.time.LocalDateTime;

public class PaymentResponseDTO {

    private Long id;
    private Long orderId;
    private Double amount;
    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;
    private LocalDateTime paymentDate;
    private String transactionReference;

    // Constructor
    public PaymentResponseDTO(Long id, Long orderId, Double amount, PaymentMethod paymentMethod,
                              PaymentStatus paymentStatus, LocalDateTime paymentDate,
                              String transactionReference) {
        this.id = id;
        this.orderId = orderId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
        this.paymentDate = paymentDate;
        this.transactionReference = transactionReference;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public Double getAmount() {
        return amount;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public String getTransactionReference() {
        return transactionReference;
    }
}