package com.example.MEDICAL_ONLINE_STORE.thisen_order.dto;

import com.example.MEDICAL_ONLINE_STORE.thisen_order.model.OrderStatus;

import java.time.LocalDateTime;

public class OrderResponseDTO {

    private Long id;
    private Long userId;
    private Double totalAmount;
    private OrderStatus status;
    private LocalDateTime orderDate;

    // OOP fields 
    private String orderType;       // "REGULAR" or "URGENT"
    private String entitySummary;   // from getEntitySummary()
    private int calculatePriority;  // from calculatePriority()

    // Constructor
    public OrderResponseDTO(Long id, Long userId, Double totalAmount, OrderStatus status,
                             LocalDateTime orderDate, String orderType,
                             String entitySummary, int calculatePriority) {
        this.id = id;
        this.userId = userId;
        this.totalAmount = totalAmount;
        this.status = status;
        this.orderDate = orderDate;
        this.orderType = orderType;
        this.entitySummary = entitySummary;
        this.calculatePriority = calculatePriority;
    }

    // Getters
    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public Double getTotalAmount() { return totalAmount; }
    public OrderStatus getStatus() { return status; }
    public LocalDateTime getOrderDate() { return orderDate; }
    public String getOrderType() { return orderType; }
    public String getEntitySummary() { return entitySummary; }
    public int getCalculatePriority() { return calculatePriority; }
}