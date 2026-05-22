package com.example.MEDICAL_ONLINE_STORE.sandali_payment.dto;

import com.example.MEDICAL_ONLINE_STORE.sandali_payment.model.PaymentMethod;
import jakarta.validation.constraints.NotNull;

public class PaymentRequestDTO {

    @NotNull(message = "Order ID is required")
    private Long orderId;

    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;

    // Optional: only used for online payments
    private String transactionReference;

    //Setters

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public void setTransactionReference(String transactionReference) {
        this.transactionReference = transactionReference;
    }


    // Getters
    public Long getOrderId() {
        return orderId;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public String getTransactionReference() {
        return transactionReference;
    }
}

