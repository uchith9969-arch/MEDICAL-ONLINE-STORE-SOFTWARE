package com.example.MEDICAL_ONLINE_STORE.keth_user.controller;

import com.example.MEDICAL_ONLINE_STORE.thisen_order.dto.OrderResponseDTO;
import com.example.MEDICAL_ONLINE_STORE.thisen_order.exception.OrderNotFoundException;
import com.example.MEDICAL_ONLINE_STORE.thisen_order.model.Order;
import com.example.MEDICAL_ONLINE_STORE.thisen_order.model.OrderStatus;
import com.example.MEDICAL_ONLINE_STORE.thisen_order.service.OrderService;
import com.example.MEDICAL_ONLINE_STORE.keth_user.model.User;
import com.example.MEDICAL_ONLINE_STORE.keth_user.service.UserService;
import com.example.MEDICAL_ONLINE_STORE.hansaja_review.model.Review;
import com.example.MEDICAL_ONLINE_STORE.hansaja_review.service.ReviewManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/customer")
@CrossOrigin(origins = "*")
public class CustomerController {

    @Autowired private OrderService orderService;
    @Autowired private UserService userService;
    @Autowired private ReviewManager reviewService;

    // Dashboard stats
    @GetMapping("/stats")
    public Map<String, Object> getStats(@RequestParam Long userId) {
        Map<String, Object> stats = new HashMap<>();
        List<Order> orders = orderService.getOrderEntitiesByUser(userId);

        Collections.reverse(orders);

        stats.put("totalOrders", orderService.getOrderByUser(userId)
                .stream()
                .filter(order -> order.getStatus() != OrderStatus.CANCELLED).count());
        stats.put("totalSpent", orders.stream().filter(order -> order.getStatus() != com.example.MEDICAL_ONLINE_STORE.thisen_order.model.OrderStatus.CANCELLED).mapToDouble(Order::getTotalAmount).sum());
        stats.put("reviewsWritten", reviewService.getReviewsByUser(userId).size());
        stats.put("recentOrders", orders.stream().limit(5).collect(Collectors.toList()));
        return stats;
    }

    // My orders — returns DTOs
    @GetMapping("/orders")
    public List<OrderResponseDTO> getOrders(@RequestParam Long userId) {
        return orderService.getOrderByUser(userId);
    }

    // Cancel my order
    @PutMapping("/orders/{id}/cancel")
    public OrderResponseDTO cancelOrder(@PathVariable Long id) throws OrderNotFoundException {
        return orderService.cancelOrder(id);
    }

    // My reviews
    @GetMapping("/reviews")
    public List<Review> getMyReviews(@RequestParam Long userId) {
        return reviewService.getReviewsByUser(userId);
    }

    // Add a new review
    @PostMapping("/reviews")
    public Review addReview(@RequestBody Review review) {
        return reviewService.addReview(review);
    }

    // Update an existing review
    @PutMapping("/reviews/{id}")
    public Review updateReview(@PathVariable Long id, @RequestBody Review updatedReview,
                               @RequestParam Long userId) {
        return reviewService.updateReview(id, updatedReview, userId);
    }

    // Delete my own review
    @DeleteMapping("/reviews/{id}")
    public Map<String, String> deleteReview(@PathVariable Long id, @RequestParam Long userId) {
        reviewService.deleteReview(id, userId);
        return Map.of("message", "Review deleted");
    }

    // Get profile
    @GetMapping("/profile")
    public User getProfile(@RequestParam Long userId) {
        return userService.getUserById(userId);
    }

    // Update profile
    @PutMapping("/profile/{userId}")
    public User updateProfile(@PathVariable Long userId, @RequestBody Map<String, String> updates) {
        User user = userService.getUserById(userId);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        // Only allow address and phoneNumber to be updated
        if (updates.containsKey("address")) {
            user.setAddress(updates.get("address"));
        }
        if (updates.containsKey("phoneNumber")) {
            user.setPhoneNumber(updates.get("phoneNumber"));
        }

        return userService.updateUser(user);
    }

    // Delete own account
    @DeleteMapping("/profile/{userId}")
    public Map<String, String> deleteAccount(@PathVariable Long userId) {
        User user = userService.getUserById(userId);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        userService.deleteUser(userId);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Account deleted successfully");
        return response;
    }
}