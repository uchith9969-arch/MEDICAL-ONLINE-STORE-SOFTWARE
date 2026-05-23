package com.example.MEDICAL_ONLINE_STORE.samod_medicine.model;

import com.example.MEDICAL_ONLINE_STORE.keth_user.model.User;  // for supplier
import jakarta.persistence.*;

@Entity
@Table(name = "medicines")
public class Medicine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    private Double price;

    @Enumerated(EnumType.STRING)
    private MedicineCategory category;

    private Integer stockQuantity;

    

    @ManyToOne
    @JoinColumn(name = "supplier_id")
    private User supplier;  // correct field name - matches User.java

    // Default constructor
    public Medicine() {
    }

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

    public void setSupplier(User supplier) {
         this.supplier = supplier;}


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

    public User getSupplier() {
        return supplier;
    }

}