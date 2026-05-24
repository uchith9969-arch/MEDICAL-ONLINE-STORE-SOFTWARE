package com.example.MEDICAL_ONLINE_STORE.thisen_order.dto;

public class OrderItemResponseDTO {

    private Long id;
    private Long orderId;
    private Long medicineId;
    private Integer quantity;
    private Double unitPrice;
    private Double totalPrice;

    // Constructor
    public OrderItemResponseDTO(Long id, Long orderId, Long medicineId, Integer quantity, Double unitPrice, Double totalPrice) {
        this.id = id;
        this.orderId = orderId;
        this.medicineId = medicineId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalPrice = totalPrice;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public Long getMedicineId() {
        return medicineId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public Double getUnitPrice() {
        return unitPrice;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }
}