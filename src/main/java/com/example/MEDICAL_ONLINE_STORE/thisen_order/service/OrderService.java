package com.example.MEDICAL_ONLINE_STORE.thisen_order.service;

import com.example.MEDICAL_ONLINE_STORE.thisen_order.dto.OrderItemRequestDTO;
import com.example.MEDICAL_ONLINE_STORE.thisen_order.dto.OrderRequestDTO;
import com.example.MEDICAL_ONLINE_STORE.thisen_order.dto.OrderResponseDTO;
import com.example.MEDICAL_ONLINE_STORE.samod_medicine.exception.MedicineNotFoundException;
import com.example.MEDICAL_ONLINE_STORE.samod_medicine.model.Medicine;
import com.example.MEDICAL_ONLINE_STORE.samod_medicine.repository.MedicineRepository;
import com.example.MEDICAL_ONLINE_STORE.samod_medicine.service.MedicineService;
import com.example.MEDICAL_ONLINE_STORE.thisen_order.exception.OrderNotFoundException;
import com.example.MEDICAL_ONLINE_STORE.thisen_order.model.*;
import com.example.MEDICAL_ONLINE_STORE.thisen_order.repository.OrderItemRepository;
import com.example.MEDICAL_ONLINE_STORE.thisen_order.repository.OrderRepository;
import com.example.MEDICAL_ONLINE_STORE.sandali_payment.model.Payment;
import com.example.MEDICAL_ONLINE_STORE.sandali_payment.model.PaymentMethod;
import com.example.MEDICAL_ONLINE_STORE.sandali_payment.model.PaymentStatus;
import com.example.MEDICAL_ONLINE_STORE.sandali_payment.repository.PaymentRepository;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private MedicineService medicineService;

    @Autowired
    private MedicineRepository medicineRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    // Helper: find order or throw checked exception
    private Order findOrderById(Long id) throws OrderNotFoundException {
        return orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with ID: " + id));
    }

    // Map Order entity -> OrderResponseDTO
    // Uses polymorphic methods: getOrderType(), getEntitySummary(),
    // calculatePriority()
    private OrderResponseDTO toResponseDTO(Order order) {
        return new OrderResponseDTO(
                order.getId(),
                order.getUserId(),
                order.getTotalAmount(),
                order.getStatus(),
                order.getOrderDate(),
                order.getOrderType(), // POLYMORPHISM
                order.getEntitySummary(), // POLYMORPHISM
                order.calculatePriority() // POLYMORPHISM
        );
    }

    // Create order — creates RegularOrder or UrgentOrder based on orderType
    public OrderResponseDTO createOrder(OrderRequestDTO requestDTO)
            throws OrderNotFoundException, MedicineNotFoundException {

        // Decide which subclass to instantiate
        Order order;
        String orderType = requestDTO.getOrderType();

        if ("URGENT".equalsIgnoreCase(orderType)) {
            order = new UrgentOrder(requestDTO.getUserId(), 500.0);
        } else {
            order = new RegularOrder(requestDTO.getUserId());
        }

        order.setTotalAmount(0.0);
        Order savedOrder = orderRepository.save(order);

        // Save items and calculate total
        if (requestDTO.getItems() != null && !requestDTO.getItems().isEmpty()) {
            for (OrderItemRequestDTO itemDTO : requestDTO.getItems()) {

                Medicine medicine = medicineService.findMedicineById(itemDTO.getMedicineId());

                if (medicine.getStockQuantity() < itemDTO.getQuantity()) {
                    throw new RuntimeException("Insufficient stock for medicine: " + medicine.getName()
                            + ". Available: " + medicine.getStockQuantity()
                            + ", Requested: " + itemDTO.getQuantity());
                }

                OrderItem item = new OrderItem(savedOrder, medicine, itemDTO.getQuantity(), medicine.getPrice());
                orderItemRepository.save(item);

                

                medicine.setStockQuantity(medicine.getStockQuantity() - itemDTO.getQuantity());

                medicineRepository.save(medicine);
            }
            recalculateOrderTotal(savedOrder);
        }
        savedOrder = orderRepository.findById(savedOrder.getId()).get();

        // If urgent, add urgency fee to total
        if (savedOrder instanceof UrgentOrder) {
            UrgentOrder urgentOrder = (UrgentOrder) savedOrder;
            savedOrder.setTotalAmount(savedOrder.getTotalAmount() + urgentOrder.getUrgencyFee());
            orderRepository.save(savedOrder);
        }
        // Automatically create payment record
        Payment payment = new Payment();

        payment.setOrder(savedOrder);
        payment.setAmount(savedOrder.getTotalAmount());

        payment.setPaymentMethod(PaymentMethod.CASH_ON_DELIVERY);

        payment.setPaymentStatus(PaymentStatus.PENDING);

        paymentRepository.save(payment);

        return toResponseDTO(findOrderById(savedOrder.getId()));
    }

    // Get all orders
    public List<OrderResponseDTO> getAllOrders() {
        return orderRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    // Get order by ID
    public OrderResponseDTO getOrderById(Long id) throws OrderNotFoundException {
        return toResponseDTO(findOrderById(id));
    }

    // Get orders by user ID
    public List<OrderResponseDTO> getOrderByUser(Long userId) {
        return orderRepository.findByUserId(userId)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }


    // Get orders by status
    public List<OrderResponseDTO> getOrdersByStatus(OrderStatus status) {
        return orderRepository.findByStatus(status)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    // Get recent orders
    public List<Order> getRecentOrders(int limit) {
        return orderRepository.findAll()
                .stream()
                .sorted((o1, o2) -> o2.getOrderDate().compareTo(o1.getOrderDate()))
                .limit(limit)
                .collect(Collectors.toList());
    }

    public Long countOrders() {
        return orderRepository.findAll()
                .stream()
                .filter(order -> order.getStatus() != OrderStatus.CANCELLED)
                .count();
    }

    // Get orders between dates
    public List<OrderResponseDTO> getOrdersBetweenDates(LocalDateTime start, LocalDateTime end) {
        return orderRepository.findByOrderDateBetween(start, end)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    // Get Order entities (not DTOs) for a user – for CustomerController
    public List<Order> getOrderEntitiesByUser(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    // Update order status
    public OrderResponseDTO updateOrderStatus(Long id, OrderStatus status)
        throws OrderNotFoundException {

    Order order = findOrderById(id);

    order.setStatus(status);

   
    if (status == OrderStatus.DELIVERED) {

        paymentRepository.findByOrderId(order.getId()).ifPresent(payment -> {

                payment.setPaymentStatus(PaymentStatus.COMPLETED);

                paymentRepository.save(payment);
        });
    }

    return toResponseDTO(orderRepository.save(order));
    }


    // Cancel order
    public OrderResponseDTO cancelOrder(Long id) throws OrderNotFoundException {
        Order order = findOrderById(id);

        if (order.getStatus() == OrderStatus.DELIVERED) {
            throw new RuntimeException("Delivered orders cannot be cancelled");
        }
        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new RuntimeException("Order is already cancelled");
        }

        order.setStatus(OrderStatus.CANCELLED);
        return toResponseDTO(orderRepository.save(order));
    }

    // Delete order
    public void deleteOrder(Long id) throws OrderNotFoundException {
        Order order = findOrderById(id);
        orderRepository.delete(order);
    }

    // Recalculate and update the total amount of an order
    public void recalculateOrderTotal(Order order) {
        List<OrderItem> items = orderItemRepository.findByOrder_Id(order.getId());
        Double total = items.stream()
                .mapToDouble(OrderItem::getTotalPrice)
                .sum();
        order.setTotalAmount(total);
        orderRepository.save(order);
    }

    public double getDeliveredRevenue() {
        return orderRepository.findAll()
                .stream()
                .filter(order -> order.getStatus() == OrderStatus.DELIVERED)
                .mapToDouble(Order::getTotalAmount)
                .sum();
    }

    public List<OrderItem> getOrdersBySupplierId(Long supplierId) {
        return orderItemRepository.findOrdersForSupplier(supplierId);
    }

    public List<Order> getOrdersByUser(Long userId) {
        return orderRepository.findByUserId(userId);
    }

}