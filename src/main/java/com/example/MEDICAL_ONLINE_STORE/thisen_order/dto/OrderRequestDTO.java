package com.example.MEDICAL_ONLINE_STORE.thisen_order.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class OrderRequestDTO {

    @NotNull(message = "User ID is required")
    private Long userId;

    // REGULAR or URGENT — defaults to REGULAR if not provided
    private String orderType = "REGULAR";

    //Client can pass items when creating the order
    @Valid
    private List<OrderItemRequestDTO> items;

    // Getters
    public Long getUserId() { return userId; }
    public String getOrderType() { return orderType; }
    public List<OrderItemRequestDTO> getItems() { return items; }

    // Setters
    public void setUserId(Long userId) { this.userId = userId; }
    public void setOrderType(String orderType) { this.orderType = orderType; }
    public void setItems(List<OrderItemRequestDTO> items) { this.items = items; }
}