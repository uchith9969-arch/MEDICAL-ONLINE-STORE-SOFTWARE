package com.example.MEDICAL_ONLINE_STORE.samod_medicine.dto;

import com.example.MEDICAL_ONLINE_STORE.samod_medicine.model.MedicineCategory;

public class MedicineResponseDTO {

    private Long id;
    private String name;
    private String description;
    private Double price;
    private MedicineCategory category;
    private Integer stockQuantity;
    private Boolean inStock;

    // Constructor
    public MedicineResponseDTO(Long id, String name, String description, Double price,
                               MedicineCategory category, Integer stockQuantity) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.stockQuantity = stockQuantity;
        this.inStock = stockQuantity != null && stockQuantity > 0;
    }

    // Getters
    public Long getId() {
        return id;
    }

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

    public Boolean getInStock() {
        return inStock;
    }
}