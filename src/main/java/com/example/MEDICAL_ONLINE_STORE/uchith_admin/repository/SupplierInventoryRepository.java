package com.example.MEDICAL_ONLINE_STORE.uchith_admin.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.MEDICAL_ONLINE_STORE.uchith_admin.model.SupplierInventory;

public interface SupplierInventoryRepository
        extends JpaRepository<SupplierInventory, Long> {

            List<SupplierInventory> findBySupplierId(Long supplierId);

            Optional<SupplierInventory>
            findBySupplierIdAndMedicineId(Long supplierId,Long medicineId);
}