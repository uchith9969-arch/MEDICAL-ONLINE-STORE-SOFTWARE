package com.example.MEDICAL_ONLINE_STORE.samod_medicine.dto;

import com.example.MEDICAL_ONLINE_STORE.samod_medicine.model.MedicineCategory;

import jakarta.validation.constraints.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class MedicineRequestDTO {

    @NotBlank(message = "Medicine name is required")
    private String name;

    private String description;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be greater than 0")
    private Double price;

    @NotNull(message = "Category is required")
    private MedicineCategory category;

    @NotNull(message = "Stock quantity is required")
    @Min(value = 0, message = "Stock quantity cannot be negative")
    private Integer stockQuantity;



    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public void setCategory(MedicineCategory category) {
        this.category = category;
    }

    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }



    // Getters
    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Double getPrice() {
        return price;
    }

    public MedicineCategory getCategory() {
        return category;
    }

    public Integer getStockQuantity() {
        return stockQuantity;
    }

    


}