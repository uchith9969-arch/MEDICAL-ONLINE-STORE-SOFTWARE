package com.example.MEDICAL_ONLINE_STORE.uchith_admin.service;

import com.example.MEDICAL_ONLINE_STORE.keth_user.model.User;
import com.example.MEDICAL_ONLINE_STORE.keth_user.repository.UserRepository;

import com.example.MEDICAL_ONLINE_STORE.samod_medicine.model.Medicine;
import com.example.MEDICAL_ONLINE_STORE.samod_medicine.repository.MedicineRepository;
import com.example.MEDICAL_ONLINE_STORE.uchith_admin.dto.SupplierInventoryRequest;
import com.example.MEDICAL_ONLINE_STORE.uchith_admin.model.SupplierInventory;
import com.example.MEDICAL_ONLINE_STORE.uchith_admin.repository.SupplierInventoryRepository;

import java.util.List;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SupplierInventoryService {

    @Autowired
    private SupplierInventoryRepository supplierInventoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MedicineRepository medicineRepository;

    public SupplierInventory addInventory(
            SupplierInventoryRequest request) {

        User supplier = userRepository.findById(request.getSupplierId())
                .orElseThrow(() -> new RuntimeException("Supplier not found"));

        Medicine medicine = medicineRepository.findById(request.getMedicineId())
                .orElseThrow(() -> new RuntimeException("Medicine not found"));

        Optional<SupplierInventory> existingInventory = supplierInventoryRepository.findBySupplierIdAndMedicineId(
                supplier.getId(),
                medicine.getId());

        SupplierInventory inventory;

        if (existingInventory.isPresent()) {

            inventory = existingInventory.get();

            inventory.setQuantity(inventory.getQuantity() + request.getQuantity());

        } else {

            inventory = new SupplierInventory();

            inventory.setSupplier(supplier);

            inventory.setMedicine(medicine);

            inventory.setQuantity(request.getQuantity());
        }

        medicine.setStockQuantity(medicine.getStockQuantity() + request.getQuantity());

        medicineRepository.save(medicine);

        return supplierInventoryRepository.save(inventory);
    }

    public List<SupplierInventory> getSupplierInventory(Long supplierId) {

        return supplierInventoryRepository.findBySupplierId(supplierId);

    }
}