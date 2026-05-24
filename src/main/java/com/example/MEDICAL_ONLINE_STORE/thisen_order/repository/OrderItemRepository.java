package com.example.MEDICAL_ONLINE_STORE.thisen_order.repository;

import com.example.MEDICAL_ONLINE_STORE.thisen_order.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    List<OrderItem> findByOrder_Id(Long orderId);

    List<OrderItem> findByMedicine_Id(Long medicineId);
    
    @Query("SELECT oi FROM OrderItem oi WHERE oi.medicine.supplier.id = :supplierId")
    List<OrderItem> findOrdersForSupplier(Long supplierId);
}