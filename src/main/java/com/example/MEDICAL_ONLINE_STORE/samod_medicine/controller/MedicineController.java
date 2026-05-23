package com.example.MEDICAL_ONLINE_STORE.samod_medicine.controller;

import com.example.MEDICAL_ONLINE_STORE.samod_medicine.dto.MedicineRequestDTO;
import com.example.MEDICAL_ONLINE_STORE.samod_medicine.dto.MedicineResponseDTO;
import com.example.MEDICAL_ONLINE_STORE.samod_medicine.exception.MedicineNotFoundException;
import com.example.MEDICAL_ONLINE_STORE.samod_medicine.model.MedicineCategory;
import com.example.MEDICAL_ONLINE_STORE.samod_medicine.service.MedicineService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/medicines")
@CrossOrigin(origins = "*") // Restrict to your frontend URL in production e.g. "http://localhost:3000"
public class MedicineController {

    @Autowired
    private MedicineService medicineService;

    // Create medicine
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MedicineResponseDTO createMedicine(@Valid @RequestBody MedicineRequestDTO requestDTO) {
        return medicineService.createMedicine(requestDTO);
    }

    // Get all medicines
    @GetMapping
    public List<MedicineResponseDTO> getAllMedicines() {
        return medicineService.getAllMedicines();
    }

    // Get medicine by ID
    @GetMapping("/{id}")
    public MedicineResponseDTO getMedicineById(@PathVariable Long id) throws MedicineNotFoundException {
        return medicineService.getMedicineById(id);
    }

    // Search medicines by name
    @GetMapping("/search")
    public List<MedicineResponseDTO> searchByName(@RequestParam String name) {
        return medicineService.searchByName(name);
    }

    // Get medicines by category
    @GetMapping("/category/{category}")
    public List<MedicineResponseDTO> getMedicinesByCategory(@PathVariable String category) {
        MedicineCategory medicineCategory;
        try {
            medicineCategory = MedicineCategory.valueOf(category.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid category: " + category);
        }
        return medicineService.getMedicinesByCategory(medicineCategory);
    }
    

    // Get low stock medicines
    @GetMapping("/low-stock")
    public List<MedicineResponseDTO> getLowStockMedicines(
            @RequestParam(defaultValue = "10") Integer threshold) {
        return medicineService.getLowStockMedicines(threshold);
    }

    // Update medicine
    @PutMapping("/{id}")
    public MedicineResponseDTO updateMedicine(
            @PathVariable Long id,
            @Valid @RequestBody MedicineRequestDTO requestDTO) throws MedicineNotFoundException {
        return medicineService.updateMedicine(id, requestDTO);
    }

    // Delete medicine
    @DeleteMapping("/{id}")
    public Map<String, String> deleteMedicine(@PathVariable Long id) throws MedicineNotFoundException {
        medicineService.deleteMedicine(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Medicine deleted successfully");
        return response;
    }

    // Restock medicine
    @PutMapping("/{id}/restock")
    public MedicineResponseDTO restockMedicine(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> body) throws MedicineNotFoundException {
        Integer quantity = body.get("quantity");
        return medicineService.restockMedicine(id, quantity);
    }
}