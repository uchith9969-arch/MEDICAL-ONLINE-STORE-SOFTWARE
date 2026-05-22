package com.example.MEDICAL_ONLINE_STORE.sandali_payment.service;

import com.example.MEDICAL_ONLINE_STORE.sandali_payment.dto.PaymentReportDTO;
import com.example.MEDICAL_ONLINE_STORE.sandali_payment.dto.PaymentRequestDTO;
import com.example.MEDICAL_ONLINE_STORE.sandali_payment.dto.PaymentResponseDTO;
import com.example.MEDICAL_ONLINE_STORE.samod_medicine.exception.MedicineNotFoundException;
import com.example.MEDICAL_ONLINE_STORE.thisen_order.exception.OrderNotFoundException;
import com.example.MEDICAL_ONLINE_STORE.sandali_payment.exception.PaymentNotFoundException;
import com.example.MEDICAL_ONLINE_STORE.sandali_payment.model.*;
import com.example.MEDICAL_ONLINE_STORE.thisen_order.repository.OrderItemRepository;
import com.example.MEDICAL_ONLINE_STORE.thisen_order.repository.OrderRepository;
import com.example.MEDICAL_ONLINE_STORE.sandali_payment.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.MEDICAL_ONLINE_STORE.samod_medicine.service.MedicineService;
import com.example.MEDICAL_ONLINE_STORE.thisen_order.model.Order;
import com.example.MEDICAL_ONLINE_STORE.thisen_order.model.OrderItem;
import com.example.MEDICAL_ONLINE_STORE.thisen_order.model.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private MedicineService medicineService;

    // Helper: find order or throw checked exception
    private Order findOrderById(Long id) throws OrderNotFoundException {
        return orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with ID: " + id));
    }

    // Helper: find payment or throw checked exception
    private Payment findPaymentById(Long id) throws PaymentNotFoundException {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found with ID: " + id));
    }

    // Map Payment entity -> PaymentResponseDTO
    private PaymentResponseDTO toResponseDTO(Payment payment) {
        return new PaymentResponseDTO(
                payment.getId(),
                payment.getOrder().getId(),
                payment.getAmount(),
                payment.getPaymentMethod(),
                payment.getPaymentStatus(),
                payment.getPaymentDate(),
                payment.getTransactionReference());
    }

    // Deduct stock for all items in an order
    private void deductStockForOrder(Order order) {
        List<OrderItem> items = orderItemRepository.findByOrder_Id(order.getId());

        for (OrderItem item : items) {
            try {
                medicineService.deductStock(item.getMedicine().getId(), item.getQuantity());
            } catch (MedicineNotFoundException e) {
                throw new RuntimeException("Medicine not found during stock deduction: " + e.getMessage());
            }
        }
    }

    // Restore stock for all items in an order (on refund)
    private void restoreStockForOrder(Order order) {
        List<OrderItem> items = orderItemRepository.findByOrder_Id(order.getId());

        for (OrderItem item : items) {
            try {
                medicineService.restoreStock(item.getMedicine().getId(), item.getQuantity());
            } catch (MedicineNotFoundException e) {
                throw new RuntimeException("Medicine not found during stock restoration: " + e.getMessage());
            }
        }
    }

    // Create a payment for an order
    public PaymentResponseDTO createPayment(PaymentRequestDTO requestDTO) throws OrderNotFoundException {
        Order order = findOrderById(requestDTO.getOrderId());

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new RuntimeException("Cannot make a payment for a cancelled order");
        }

        if (order.getTotalAmount() == null || order.getTotalAmount() <= 0) {
            throw new RuntimeException("Cannot make a payment for an order with no items");
        }
        if (paymentRepository.existsByOrderId(order.getId())) {
            throw new RuntimeException("A payment already exists for order ID: " + order.getId());
        }

        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setAmount(order.getTotalAmount());
        payment.setPaymentMethod(requestDTO.getPaymentMethod());
        payment.setTransactionReference(requestDTO.getTransactionReference());

        // Cash on Delivery: mark as COMPLETED immediately + deduct stock
        // Online: start as PENDING until confirmed
        if (requestDTO.getPaymentMethod() == PaymentMethod.CASH_ON_DELIVERY) {
            payment.setPaymentStatus(PaymentStatus.COMPLETED);

            deductStockForOrder(order);

        } else {
            payment.setPaymentStatus(PaymentStatus.PENDING);
        }

        return toResponseDTO(paymentRepository.save(payment));
    }

    // Confirm an online payment
    public PaymentResponseDTO confirmPayment(Long paymentId, String transactionReference)
            throws PaymentNotFoundException {
        Payment payment = findPaymentById(paymentId);

        if (payment.getPaymentStatus() == PaymentStatus.COMPLETED) {
            throw new RuntimeException("Payment is already completed");
        }
        if (payment.getPaymentStatus() == PaymentStatus.FAILED) {
            throw new RuntimeException("Cannot confirm a failed payment");
        }
        if (payment.getPaymentMethod() != PaymentMethod.ONLINE) {
            throw new RuntimeException("Only online payments need confirmation");
        }

        payment.setPaymentStatus(PaymentStatus.COMPLETED);
        payment.setTransactionReference(transactionReference);

        Order order = payment.getOrder();

        deductStockForOrder(order);

        return toResponseDTO(paymentRepository.save(payment));
    }

    // Mark an online payment as FAILED
    public PaymentResponseDTO failPayment(Long paymentId) throws PaymentNotFoundException {
        Payment payment = findPaymentById(paymentId);

        if (payment.getPaymentStatus() != PaymentStatus.PENDING) {
            throw new RuntimeException("Only pending payments can be marked as failed");
        }

        payment.setPaymentStatus(PaymentStatus.FAILED);
        return toResponseDTO(paymentRepository.save(payment));
    }

    // Refund a completed payment
    public PaymentResponseDTO refundPayment(Long paymentId) throws PaymentNotFoundException {
        Payment payment = findPaymentById(paymentId);

        if (payment.getPaymentStatus() != PaymentStatus.COMPLETED) {
            throw new RuntimeException("Only completed payments can be refunded");
        }

        payment.setPaymentStatus(PaymentStatus.REFUNDED);

        Order order = payment.getOrder();
        restoreStockForOrder(order);
        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);

        return toResponseDTO(paymentRepository.save(payment));
    }

    // Get payment by ID
    public PaymentResponseDTO getPaymentById(Long id) throws PaymentNotFoundException {
        return toResponseDTO(findPaymentById(id));
    }

    // Get payment by order ID
    public PaymentResponseDTO getPaymentByOrderId(Long orderId) throws OrderNotFoundException {
        findOrderById(orderId);
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("No payment found for order ID: " + orderId));
        return toResponseDTO(payment);
    }

    // Get all payments
    public List<PaymentResponseDTO> getAllPayments() {
        return paymentRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    // Get payments by status
    public List<PaymentResponseDTO> getPaymentsByStatus(PaymentStatus status) {
        return paymentRepository.findByPaymentStatus(status)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    // Get payments by method
    public List<PaymentResponseDTO> getPaymentsByMethod(PaymentMethod method) {
        return paymentRepository.findByPaymentMethod(method)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    // Generate payment report between two dates
    public PaymentReportDTO getPaymentReport(LocalDateTime start, LocalDateTime end) {
        List<Payment> payments = paymentRepository.findByPaymentDateBetween(start, end);

        Double totalRevenue = payments.stream()
                .filter(p -> p.getOrder() != null)
                .filter(p -> p.getOrder().getStatus() == OrderStatus.DELIVERED)
                .mapToDouble(Payment::getAmount)
                .sum();

        Long totalPayments = (long) payments.size();

        Map<String, Long> countByStatus = payments.stream()
                .collect(Collectors.groupingBy(
                        p -> p.getPaymentStatus().name(),
                        Collectors.counting()));

        Map<String, Long> countByMethod = payments.stream()
                .collect(Collectors.groupingBy(
                        p -> p.getPaymentMethod().name(),
                        Collectors.counting()));

        return new PaymentReportDTO(start, end, totalRevenue, totalPayments, countByStatus, countByMethod);
    }


}
