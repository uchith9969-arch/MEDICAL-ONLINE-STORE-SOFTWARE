package com.example.MEDICAL_ONLINE_STORE.thisen_order.model;

import jakarta.persistence.*;

/**
 * UrgentOrder — extends Order
 * High priority order with express delivery and extra fee.
 */

@Entity
@Table(name = "urgent_orders")
@PrimaryKeyJoinColumn(name = "order_id")
public class UrgentOrder extends Order {

    // Extra fee charged for urgent/express delivery
    private Double urgencyFee;

    // Express delivery in 1 day
    private Integer estimatedDeliveryDays;

    public UrgentOrder() {
        super();
        this.urgencyFee = 500.0; // LKR 500 extra
        this.estimatedDeliveryDays = 1;
    }

    public UrgentOrder(Long userId, Double urgencyFee) {
        super(userId);
        this.urgencyFee = urgencyFee;
        this.estimatedDeliveryDays = 1;
    }

    //Overrides Order's getOrderType()
    @Override
    public String getOrderType() {
        return "URGENT";
    }

    //Overrides Order's calculatePriority()
    @Override
    public int calculatePriority() {
        return 10; // highest priority
    }

    //Overrides BaseEntity's getEntitySummary()
    @Override
    public String getEntitySummary() {
        return "Urgent Order #" + getId() + " | User: " + getUserId() +
               " | Total: LKR " + getTotalAmount() +
               " | Urgency Fee: LKR " + urgencyFee +
               " | Express Delivery in " + estimatedDeliveryDays + " day";
    }

    // Total amount including urgency fee
    public Double getTotalWithFee() {
        return getTotalAmount() + urgencyFee;
    }

    // Getters
    public Double getUrgencyFee() { return urgencyFee; }
    public Integer getEstimatedDeliveryDays() { return estimatedDeliveryDays; }

    // Setters
    public void setUrgencyFee(Double urgencyFee) { this.urgencyFee = urgencyFee; }
    public void setEstimatedDeliveryDays(Integer days) { this.estimatedDeliveryDays = days; }
}