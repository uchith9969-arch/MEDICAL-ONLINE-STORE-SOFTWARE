package com.example.MEDICAL_ONLINE_STORE.uchith_admin.controller;

import com.example.MEDICAL_ONLINE_STORE.samod_medicine.dto.MedicineRequestDTO;
import com.example.MEDICAL_ONLINE_STORE.samod_medicine.dto.MedicineResponseDTO;
import com.example.MEDICAL_ONLINE_STORE.samod_medicine.exception.MedicineNotFoundException;
import com.example.MEDICAL_ONLINE_STORE.samod_medicine.model.Medicine;
import com.example.MEDICAL_ONLINE_STORE.samod_medicine.service.MedicineService;
import com.example.MEDICAL_ONLINE_STORE.thisen_order.model.OrderItem;
import com.example.MEDICAL_ONLINE_STORE.thisen_order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/supplier")
@CrossOrigin(origins = "*")
public class SupplierController {

    @Autowired private MedicineService medicineService;
    @Autowired private OrderService orderService;

    @GetMapping("/stats")
    public Map<String, Object> getStats(@RequestParam Long supplierId) {
        Map<String, Object> stats = new HashMap<>();
        List<Medicine> medicines = medicineService.getMedicinesBySupplierId(supplierId);
        stats.put("totalMedicines", medicines.size());
        stats.put("lowStockCount", medicines.stream().filter(m -> m.getStockQuantity() < 10).count());
        stats.put("totalOrders", orderService.getOrdersBySupplierId(supplierId).size());

        return stats;
    }


    @GetMapping("/medicines")
    public List<MedicineResponseDTO> getMyMedicines(@RequestParam Long supplierId) {
        return medicineService.getMedicinesBySupplierId(supplierId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @PostMapping("/medicines")
    public MedicineResponseDTO addMedicine(@RequestBody MedicineRequestDTO dto, @RequestParam Long supplierId) {
        return medicineService.createMedicineForSupplier(dto, supplierId);
    }

    @PutMapping("/medicines/{id}")
    public MedicineResponseDTO updateMedicine(@PathVariable Long id, @RequestBody MedicineRequestDTO dto,
                                              @RequestParam Long supplierId) throws MedicineNotFoundException {
        Medicine existing = medicineService.findMedicineById(id);
        if (!existing.getSupplier().getId().equals(supplierId))
            throw new RuntimeException("Not authorized");
        return medicineService.updateMedicine(id, dto);
    }

    @DeleteMapping("/medicines/{id}")
    public Map<String, String> deleteMedicine(@PathVariable Long id, @RequestParam Long supplierId) throws MedicineNotFoundException {
        Medicine existing = medicineService.findMedicineById(id);
        if (!existing.getSupplier().getId().equals(supplierId))
            throw new RuntimeException("Not authorized");
        medicineService.deleteMedicine(id);
        return Map.of("message", "Deleted");
    }

    @GetMapping("/orders")
    public List<Map<String, Object>> getOrdersForSupplier(@RequestParam Long supplierId) {

        List<OrderItem> items = orderService.getOrdersBySupplierId(supplierId);

        return items.stream().map(item -> {

            Map<String, Object> map = new HashMap<>();

            map.put("id", item.getId());
            map.put("orderId", item.getOrder().getId());
            map.put("medicineName", item.getMedicine().getName());
            map.put("quantity", item.getQuantity());
            map.put("unitPrice", item.getUnitPrice());
            map.put("status", item.getOrder().getStatus());

            return map;

        }).collect(Collectors.toList());
    }

    private MedicineResponseDTO toDTO(Medicine m) {
        return new MedicineResponseDTO(m.getId(), m.getName(), m.getDescription(), m.getPrice(),
                m.getCategory(), m.getStockQuantity());
    }
}