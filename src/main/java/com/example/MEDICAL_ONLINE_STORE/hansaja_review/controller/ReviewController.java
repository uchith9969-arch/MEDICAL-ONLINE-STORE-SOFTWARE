package com.example.MEDICAL_ONLINE_STORE.hansaja_review.controller;

import com.example.MEDICAL_ONLINE_STORE.hansaja_review.model.Review;
import com.example.MEDICAL_ONLINE_STORE.hansaja_review.service.ReviewManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
    private ReviewManager manager;

    // GET: All reviews
    @GetMapping
    public ResponseEntity<List<Review>> getAll() {
        List<Review> reviews = manager.getAll();
        return ResponseEntity.ok(reviews); // HTTP 200 OK සමඟ දත්ත යවයි
    }

    // POST: Add new review
    @PostMapping // <-- FIX: දැන් Frontend එකට මේක හරහා දත්ත එවන්න පුළුවන්
    public ResponseEntity<Review> add(@RequestBody Review review) {
        Review savedReview = manager.addReview(review);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedReview); // HTTP 201 Created
    }

    // PUT: Update review
    @PutMapping("/{id}")
    public ResponseEntity<Review> update(@PathVariable Long id, @RequestBody Review review) {
        // FIX: රික්වෙස්ට් එකේ userId එක null වීමේ අවදානම මඟහරවා ගැනීමට manager එක ඇතුලෙන්ම ඒක චෙක් කරගන්න දීම වඩා සුදුසුයි
        Review updatedReview = manager.updateReview(id, review, review.getUserId());
        return ResponseEntity.ok(updatedReview);
    }

    // DELETE: Remove review
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        manager.delete(id);
        return ResponseEntity.ok("Deleted Successfully"); // HTTP 200 OK සමඟ Text එකක් යවයි
    }
}

