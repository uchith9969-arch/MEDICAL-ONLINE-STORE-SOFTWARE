package com.example.MEDICAL_ONLINE_STORE.uchith_admin.controller;

import com.example.MEDICAL_ONLINE_STORE.samod_medicine.dto.MedicineRequestDTO;
import com.example.MEDICAL_ONLINE_STORE.samod_medicine.dto.MedicineResponseDTO;
import com.example.MEDICAL_ONLINE_STORE.samod_medicine.exception.MedicineNotFoundException;
import com.example.MEDICAL_ONLINE_STORE.samod_medicine.service.MedicineService;
import com.example.MEDICAL_ONLINE_STORE.thisen_order.dto.OrderResponseDTO;
import com.example.MEDICAL_ONLINE_STORE.thisen_order.exception.OrderNotFoundException;
import com.example.MEDICAL_ONLINE_STORE.thisen_order.model.OrderStatus;
import com.example.MEDICAL_ONLINE_STORE.thisen_order.service.OrderService;
import com.example.MEDICAL_ONLINE_STORE.sandali_payment.service.PaymentService;
import com.example.MEDICAL_ONLINE_STORE.keth_user.dto.SignupRequest;
import com.example.MEDICAL_ONLINE_STORE.keth_user.model.User;
import com.example.MEDICAL_ONLINE_STORE.keth_user.service.UserService;
import com.example.MEDICAL_ONLINE_STORE.hansaja_review.model.Review;
import com.example.MEDICAL_ONLINE_STORE.hansaja_review.service.ReviewManager;
import com.example.MEDICAL_ONLINE_STORE.uchith_admin.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    @Autowired private UserService userService;
    @Autowired private OrderService orderService;
    @Autowired private MedicineService medicineService;
    @Autowired private PaymentService paymentService;
    @Autowired private ReviewManager reviewService;
    @Autowired private FileStorageService fileStorageService;

    // Dashboard stats
    @GetMapping("/stats")
    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", userService.getAllUsers().size());
        stats.put("totalOrders", orderService.getAllOrders()
                        .stream()
                        .filter(order -> order.getStatus() != OrderStatus.CANCELLED).count());
        stats.put("totalRevenue", orderService.getDeliveredRevenue());
        stats.put("totalMedicineStock", medicineService.getTotalMedicineStock());
        stats.put("recentOrders", orderService.getRecentOrders(5));
        return stats;
    }

    // --- User Management ---
    @GetMapping("/users")
    public List<User> getAllUsers() { return userService.getAllUsers(); }

    @DeleteMapping("/users/{id}")
    public Map<String, String> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return Map.of("message", "User deleted");
    }

    @PostMapping("/users")
    public ResponseEntity<?> createUser(@RequestBody SignupRequest request) {
    // 🔒 Only this email can be ADMIN
        if (request.getRole() == User.Role.ADMIN && !"Uchith9969@gmail.com".equals(request.getEmail())) {
            return ResponseEntity.badRequest() .body(Map.of("message", "Only Uchith9969@gmail.com can be assigned the ADMIN role"));
    }
    userService.registerUser(request);
    return ResponseEntity.ok(userService.findByEmail(request.getEmail()));
}

    // --- Order Management ---
    @GetMapping("/orders")
    public List<OrderResponseDTO> getAllOrders() { return orderService.getAllOrders(); }

    @PutMapping("/orders/{id}/status")
    public OrderResponseDTO updateOrderStatus(@PathVariable Long id,
            @RequestBody Map<String, String> body) throws OrderNotFoundException {
        return orderService.updateOrderStatus(id,
                OrderStatus.valueOf(body.get("status").toUpperCase()));
    }

    @PutMapping("/orders/{id}/cancel")
    public OrderResponseDTO cancelOrder(@PathVariable Long id) throws OrderNotFoundException {
        return orderService.cancelOrder(id);
    }

    @DeleteMapping("/orders/{id}")
    public Map<String, String> deleteOrder(@PathVariable Long id) throws OrderNotFoundException {
        orderService.deleteOrder(id);
        return Map.of("message", "Order deleted");
    }

    // --- Medicine Management ---
    @GetMapping("/medicines")
    public List<MedicineResponseDTO> getAllMedicines() { return medicineService.getAllMedicines(); }

    @PostMapping("/medicines")
    public MedicineResponseDTO createMedicine(@RequestBody MedicineRequestDTO dto) {
        return medicineService.createMedicine(dto);
    }

    @DeleteMapping("/medicines/{id}")
    public Map<String, String> deleteMedicine(@PathVariable Long id) throws MedicineNotFoundException {
        medicineService.deleteMedicine(id);
        return Map.of("message", "Medicine deleted");
    }

    // --- Review Management ---

    @PutMapping("/reviews/{id}")
    public ResponseEntity<?> updateReview(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Review existing = reviewService.getAll().stream().filter(r -> r.getId().equals(id)).findFirst().orElse(null);
        if (existing == null) return ResponseEntity.notFound().build();
        if (body.containsKey("rating"))  existing.setRating((Integer) body.get("rating"));
        if (body.containsKey("comment")) existing.setComment((String) body.get("comment"));
        return ResponseEntity.ok(reviewService.addReview(existing)); // addReview = save()
    }

    @GetMapping("/reviews")
    public List<Review> getAllReviews() { return reviewService.getAll(); }

    @DeleteMapping("/reviews/{id}")
    public Map<String, String> deleteReview(@PathVariable Long id) {
        reviewService.delete(id);
        return Map.of("message", "Review deleted by admin");
    }

    // --- File Storage Logs ---
    @GetMapping("/file-logs")
    public String getFileLogs() { return fileStorageService.readAllLogs(); }
}