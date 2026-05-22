package com.example.MEDICAL_ONLINE_STORE.hansaja_review.repository;

import com.example.MEDICAL_ONLINE_STORE.hansaja_review.model.Review;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByUserId(Long userId);
    List<Review> findByMedicineId(Long medicineId);
}