package com.example.MEDICAL_ONLINE_STORE.sandali_payment.model;

import com.example.MEDICAL_ONLINE_STORE.thisen_order.model.Order;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    // Link to the order
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    @JsonIgnoreProperties({"hibernateLazyInitializer","handler","orderItems","payment"})
    private Order order;

    // Amount is taken from the order, not from the client
    private Double amount;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    // FIX: @JsonFormat so Jackson serializes as "2026-05-18T10:30:00" not [2026,5,18,10,30,0]
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime paymentDate;

    // Transaction reference for online payments
    private String transactionReference;

    // Default constructor
    public Payment() {
        this.paymentDate = LocalDateTime.now();
    }

    // Setters
    public void setOrder(Order order) { this.order = order; }
    public void setAmount(Double amount) { this.amount = amount; }
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }
    public void setPaymentStatus(PaymentStatus paymentStatus) { this.paymentStatus = paymentStatus; }
    public void setPaymentDate(LocalDateTime paymentDate) { this.paymentDate = paymentDate; }
    public void setTransactionReference(String transactionReference) { this.transactionReference = transactionReference; }

    // Getters
    public Long getId() { return id; }
    public Order getOrder() { return order; }
    public Double getAmount() { return amount; }
    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public PaymentStatus getPaymentStatus() { return paymentStatus; }
    public LocalDateTime getPaymentDate() { return paymentDate; }
    public String getTransactionReference() { return transactionReference; }
}