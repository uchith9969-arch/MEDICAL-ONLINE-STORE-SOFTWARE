package com.example.MEDICAL_ONLINE_STORE.thisen_order.model;

import com.example.MEDICAL_ONLINE_STORE.samod_medicine.model.Medicine;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne
    @JoinColumn(name = "medicine_id")
    private Medicine medicine;

    private Integer quantity;

    private Double unitPrice;

    public OrderItem() {
    }

    public OrderItem(Order order, Medicine medicine, Integer quantity, Double unitPrice) {
        this.order = order;
        this.medicine = medicine;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public Double getTotalPrice() {
        return quantity * unitPrice;
    }

    public Long getId() {
        return id;
    }

    public Order getOrder() {
        return order;
    }

    public Medicine getMedicine() {
        return medicine;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public Double getUnitPrice() {
        return unitPrice;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public void setMedicine(Medicine medicine) {
        this.medicine = medicine;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public void setUnitPrice(Double unitPrice) {
        this.unitPrice = unitPrice;
    }
}