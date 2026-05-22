package com.example.MEDICAL_ONLINE_STORE.hansaja_review.service;

import com.example.MEDICAL_ONLINE_STORE.hansaja_review.model.Review;
import com.example.MEDICAL_ONLINE_STORE.hansaja_review.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewManager {

    @Autowired
    private ReviewRepository reviewRepository;

    // Get all reviews (for admin)
    public List<Review> getAll() {
        return reviewRepository.findAll();
    }

    // Get reviews by user ID (for customer dashboard)
    public List<Review> getReviewsByUser(Long userId) {
        return reviewRepository.findByUserId(userId);
    }

    // Get reviews by medicine ID
    public List<Review> getReviewsByMedicine(Long medicineId) {
        return reviewRepository.findByMedicineId(medicineId);
    }

    // Add a new review
    public Review addReview(Review review) {
        return reviewRepository.save(review);
    }

    // Update review – only if userId matches (for customer)
    public Review updateReview(Long id, Review updatedReview, Long userId) {
        Review existing = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Review not found"));
        if (!existing.getUserId().equals(userId)) {
            throw new RuntimeException("You can only update your own reviews");
        }
        existing.setRating(updatedReview.getRating());
        existing.setComment(updatedReview.getComment());
        // do not change medicineId or userId
        return reviewRepository.save(existing);
    }

    // Delete review by ID – admin use (no user check)
    public void delete(Long id) {
        reviewRepository.deleteById(id);
    }

    // Delete review by ID and userId – for customer
    public void deleteReview(Long id, Long userId) {
        Review existing = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Review not found"));
        if (!existing.getUserId().equals(userId)) {
            throw new RuntimeException("You can only delete your own reviews");
        }
        reviewRepository.deleteById(id);
    }
}