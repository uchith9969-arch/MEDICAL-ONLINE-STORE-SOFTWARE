package com.example.MEDICAL_ONLINE_STORE.sandali_payment.controller;

import com.example.MEDICAL_ONLINE_STORE.sandali_payment.dto.PaymentReportDTO;
import com.example.MEDICAL_ONLINE_STORE.sandali_payment.dto.PaymentRequestDTO;
import com.example.MEDICAL_ONLINE_STORE.sandali_payment.dto.PaymentResponseDTO;
import com.example.MEDICAL_ONLINE_STORE.thisen_order.exception.OrderNotFoundException;
import com.example.MEDICAL_ONLINE_STORE.sandali_payment.exception.PaymentNotFoundException;
import com.example.MEDICAL_ONLINE_STORE.sandali_payment.model.PaymentMethod;
import com.example.MEDICAL_ONLINE_STORE.sandali_payment.model.PaymentStatus;
import com.example.MEDICAL_ONLINE_STORE.sandali_payment.service.PaymentService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "*") // Restricting the frontend URL in production (e.g. "http://localhost:3000")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    // Create a payment for an order
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentResponseDTO createPayment(@Valid @RequestBody PaymentRequestDTO requestDTO) throws OrderNotFoundException {
        return paymentService.createPayment(requestDTO);
    }

    // Get payment by ID
    @GetMapping("/{id}")
    public PaymentResponseDTO getPaymentById(@PathVariable Long id) throws PaymentNotFoundException {
        return paymentService.getPaymentById(id);
    }

    // Get payment by order ID
    @GetMapping("/order/{orderId}")
    public PaymentResponseDTO getPaymentByOrderId(@PathVariable Long orderId) throws OrderNotFoundException {
        return paymentService.getPaymentByOrderId(orderId);
    }

    // Get all payments
    @GetMapping
    public List<PaymentResponseDTO> getAllPayments() {
        return paymentService.getAllPayments();
    }

    // Get payments by status
    @GetMapping("/status/{status}")
    public List<PaymentResponseDTO> getPaymentsByStatus(@PathVariable String status) {
        PaymentStatus paymentStatus;
        try {
            paymentStatus = PaymentStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid payment status: " + status);
        }
        return paymentService.getPaymentsByStatus(paymentStatus);
    }

    // Get payments by method
    @GetMapping("/method/{method}")
    public List<PaymentResponseDTO> getPaymentsByMethod(@PathVariable String method) {
        PaymentMethod paymentMethod;
        try {
            paymentMethod = PaymentMethod.valueOf(method.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid payment method: " + method);
        }
        return paymentService.getPaymentsByMethod(paymentMethod);
    }

    // Confirm an online payment
    @PutMapping("/{id}/confirm")
    public PaymentResponseDTO confirmPayment(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) throws PaymentNotFoundException {
        String transactionReference = body != null ? body.get("transactionReference") : null;
        if (transactionReference == null || transactionReference.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Transaction reference is required");
        }
        return paymentService.confirmPayment(id, transactionReference);
    }

    // Mark a payment as failed
    @PutMapping("/{id}/fail")
    public PaymentResponseDTO failPayment(@PathVariable Long id) throws PaymentNotFoundException {
        return paymentService.failPayment(id);
    }

    // Refund a payment
    @PutMapping("/{id}/refund")
    public PaymentResponseDTO refundPayment(@PathVariable Long id) throws PaymentNotFoundException {
        return paymentService.refundPayment(id);
    }

    // Get payment report between two dates
    @GetMapping("/report")
    public PaymentReportDTO getPaymentReport(
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

        return paymentService.getPaymentReport(startDate, endDate);
    }
}