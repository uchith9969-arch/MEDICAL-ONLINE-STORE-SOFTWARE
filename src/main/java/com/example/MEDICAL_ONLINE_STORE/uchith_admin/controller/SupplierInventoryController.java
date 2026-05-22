package com.example.MEDICAL_ONLINE_STORE.uchith_admin.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.MEDICAL_ONLINE_STORE.uchith_admin.dto.SupplierInventoryRequest;
import com.example.MEDICAL_ONLINE_STORE.uchith_admin.model.SupplierInventory;
import com.example.MEDICAL_ONLINE_STORE.uchith_admin.service.SupplierInventoryService;

@RestController
@RequestMapping("/api/supplier/inventory")
@CrossOrigin
public class SupplierInventoryController {

    @Autowired
    private SupplierInventoryService supplierInventoryService;

    @PostMapping
    public ResponseEntity<SupplierInventory> addInventory(
            @RequestBody SupplierInventoryRequest request) {

        SupplierInventory savedInventory = supplierInventoryService.addInventory(request);

        return ResponseEntity.ok(savedInventory);
    }

    @GetMapping("/{supplierId}")
    public ResponseEntity<List<SupplierInventory>>
    getSupplierInventory(@PathVariable Long supplierId) {

        List<SupplierInventory> inventoryList = supplierInventoryService.getSupplierInventory(supplierId);

        return ResponseEntity.ok(inventoryList);
}
}