package com.example.MEDICAL_ONLINE_STORE.thisen_order.model;

import jakarta.persistence.*;

/**
 * RegularOrder — extends Order.
 * Standard order with normal delivery.
 */

@Entity
@Table(name = "regular_orders")
@PrimaryKeyJoinColumn(name = "order_id")
public class RegularOrder extends Order {

    // Standard delivery days
    private Integer estimatedDeliveryDays;

    public RegularOrder() {
        super();
        this.estimatedDeliveryDays = 5;
    }

    public RegularOrder(Long userId) {
        super(userId);
        this.estimatedDeliveryDays = 5;
    }

    //Overrides Order's getOrderType()
    @Override
    public String getOrderType() {
        return "REGULAR";
    }

    //Overrides Order's calculatePriority()
    @Override
    public int calculatePriority() {
        return 1; // lowest priority
    }

    // Overrides BaseEntity's getEntitySummary()
    @Override
    public String getEntitySummary() {
        return "Regular Order #" + getId() + " | User: " + getUserId() +
               " | Total: LKR " + getTotalAmount() +
               " | Delivery in " + estimatedDeliveryDays + " days";
    }

    // Getter
    public Integer getEstimatedDeliveryDays() {
        return estimatedDeliveryDays;
    }

    // Setter
    public void setEstimatedDeliveryDays(Integer estimatedDeliveryDays) {
        this.estimatedDeliveryDays = estimatedDeliveryDays;
    }
}