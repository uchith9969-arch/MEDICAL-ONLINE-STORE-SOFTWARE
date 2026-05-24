package com.example.MEDICAL_ONLINE_STORE.thisen_order.controller;

import com.example.MEDICAL_ONLINE_STORE.thisen_order.dto.OrderRequestDTO;
import com.example.MEDICAL_ONLINE_STORE.thisen_order.dto.OrderResponseDTO;
import com.example.MEDICAL_ONLINE_STORE.thisen_order.exception.OrderNotFoundException;
import com.example.MEDICAL_ONLINE_STORE.thisen_order.model.OrderItem;
import com.example.MEDICAL_ONLINE_STORE.thisen_order.model.OrderStatus;
import com.example.MEDICAL_ONLINE_STORE.thisen_order.repository.OrderItemRepository;
import com.example.MEDICAL_ONLINE_STORE.thisen_order.service.OrderService;
import com.example.MEDICAL_ONLINE_STORE.samod_medicine.exception.MedicineNotFoundException;
import com.example.MEDICAL_ONLINE_STORE.samod_medicine.model.Medicine;
import com.example.MEDICAL_ONLINE_STORE.samod_medicine.service.MedicineService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*") // Restricting the frontend URL in production (e.g. "http://localhost:3000")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderItemRepository orderItemRepository;
    @Autowired
    private MedicineService medicineService;

    // Create order
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponseDTO createOrder(@Valid @RequestBody OrderRequestDTO requestDTO)
            throws OrderNotFoundException, MedicineNotFoundException {
        return orderService.createOrder(requestDTO);
    }

    // Get all orders
    @GetMapping
    public List<OrderResponseDTO> getAllOrders() {
        return orderService.getAllOrders();
    }

    // Get order by ID
    @GetMapping("/{id}")
    public OrderResponseDTO getOrderById(@PathVariable Long id) throws OrderNotFoundException {
        return orderService.getOrderById(id);
    }

    // Get orders by user ID
    @GetMapping("/user/{userId}")
    public List<OrderResponseDTO> getOrderByUser(@PathVariable Long userId) {
        return orderService.getOrderByUser(userId);
    }

    // Get orders by status
    @GetMapping("/status/{status}")
    public List<OrderResponseDTO> getOrderByStatus(@PathVariable String status) {
        OrderStatus orderStatus;
        try {
            orderStatus = OrderStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid order status: " + status);
        }
        return orderService.getOrdersByStatus(orderStatus);
    }

    // Get orders between dates
    @GetMapping("/date-range")
    public List<OrderResponseDTO> getOrdersBetweenDates(
            @RequestParam String start,
            @RequestParam String end) {

        LocalDateTime startDate;
        LocalDateTime endDate;

        try {
            startDate = LocalDate.parse(start).atStartOfDay();
            endDate = LocalDate.parse(end).atTime(23, 59, 59);
        } catch (DateTimeParseException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid date format. Use yyyy-MM-dd");
        }

        if (startDate.isAfter(endDate)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Start date must be before end date");
        }

        return orderService.getOrdersBetweenDates(startDate, endDate);
    }

    // Supplier status
    // Add this new endpoint
    @PutMapping("/{id}/supplier-status")
    public ResponseEntity<?> supplierUpdateStatus(
            @PathVariable Long id,
            @RequestParam Long supplierId,
            @RequestBody Map<String, String> body) throws OrderNotFoundException {

        // Only allow PROCESSING, SHIPPED, DELIVERED
        String statusStr = body.get("status");
        if (!List.of("PROCESSING", "SHIPPED", "DELIVERED").contains(statusStr.toUpperCase())) {
            return ResponseEntity.badRequest().body("Suppliers can only set PROCESSING, SHIPPED or DELIVERED");
        }

        // Check this order actually contains this supplier's medicine
        List<OrderItem> items = orderItemRepository.findByOrder_Id(id);

        boolean owned = items.stream().anyMatch(item -> {

            Medicine m = item.getMedicine();

            return m != null && m.getSupplier() != null && supplierId.equals(m.getSupplier().getId());
        });

        if (!owned) {
           return ResponseEntity.status(403).body("This order does not contain your medicines");
        }

        OrderStatus status = OrderStatus.valueOf(statusStr.toUpperCase());
        return ResponseEntity.ok(orderService.updateOrderStatus(id, status));
    }

    // Update order status
    @PutMapping("/{id}/status")
    public OrderResponseDTO updateStatus(@PathVariable Long id, @RequestBody Map<String, String> body)
            throws OrderNotFoundException {
        if (body == null || !body.containsKey("status")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status is required");
        }
        String statusStr = body.get("status");
        OrderStatus status;
        try {
            status = OrderStatus.valueOf(statusStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid status value: " + statusStr);
        }
        return orderService.updateOrderStatus(id, status);
    }

    // Cancel order
    @PutMapping("/{id}/cancel")
    public OrderResponseDTO cancelOrder(@PathVariable Long id) throws OrderNotFoundException {
        return orderService.cancelOrder(id);
    }

    // Delete order
    @DeleteMapping("/{id}")
    public Map<String, String> deleteOrder(@PathVariable Long id) throws OrderNotFoundException {
        orderService.deleteOrder(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Order deleted successfully");
        return response;
    }
}