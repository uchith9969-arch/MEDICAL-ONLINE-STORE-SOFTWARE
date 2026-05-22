package com.example.MEDICAL_ONLINE_STORE.sandali_payment.dto;

import java.time.LocalDateTime;
import java.util.Map;

public class PaymentReportDTO {

    private LocalDateTime from;
    private LocalDateTime to;
    private Double totalRevenue;
    private Long totalPayments;
    private Map<String, Long> countByStatus;
    private Map<String, Long> countByMethod;

    // Constructor
    public PaymentReportDTO(LocalDateTime from, LocalDateTime to, Double totalRevenue,
                            Long totalPayments, Map<String, Long> countByStatus,
                            Map<String, Long> countByMethod) {
        this.from = from;
        this.to = to;
        this.totalRevenue = totalRevenue;
        this.totalPayments = totalPayments;
        this.countByStatus = countByStatus;
        this.countByMethod = countByMethod;
    }

    // Getters
    public LocalDateTime getFrom() { return from; }
    public LocalDateTime getTo() { return to; }
    public Double getTotalRevenue() { return totalRevenue; }
    public Long getTotalPayments() { return totalPayments; }
    public Map<String, Long> getCountByStatus() { return countByStatus; }
    public Map<String, Long> getCountByMethod() { return countByMethod; }
}