package com.example.MEDICAL_ONLINE_STORE.samod_medicine.repository;

import com.example.MEDICAL_ONLINE_STORE.samod_medicine.model.Medicine;
import com.example.MEDICAL_ONLINE_STORE.samod_medicine.model.MedicineCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MedicineRepository extends JpaRepository<Medicine, Long> {

    // Search medicines by name (case-insensitive)
    List<Medicine> findByNameContainingIgnoreCase(String name);

    // Get medicines by category
    List<Medicine> findByCategory(MedicineCategory category);

    // Get medicines with low stock (below a threshold)
    List<Medicine> findByStockQuantityLessThanEqual(Integer threshold);

    List<Medicine> findBySupplierId(Long supplierId);
}