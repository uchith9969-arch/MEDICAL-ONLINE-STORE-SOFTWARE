package com.example.MEDICAL_ONLINE_STORE.thisen_order.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;


public class OrderItemRequestDTO {

    @NotNull(message = "Medicine ID is required")
    private Long medicineId;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be atleast 1")  // unitPrice removed — price is fetched automatically from Medicine
    private Integer quantity;

    

    // Getters
    public Long getMedicineId() {
        return medicineId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    

    // Setters
    public void setMedicineId(Long medicineId) {
        this.medicineId = medicineId;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

  
    
}