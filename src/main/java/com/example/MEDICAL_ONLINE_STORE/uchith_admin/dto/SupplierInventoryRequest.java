package com.example.MEDICAL_ONLINE_STORE.uchith_admin.dto;

public class SupplierInventoryRequest {

    private Long supplierId;
    private Long medicineId;
    private Integer quantity;

    public SupplierInventoryRequest() {
    }

    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }

    public Long getMedicineId() {
        return medicineId;
    }

    public void setMedicineId(Long medicineId) {
        this.medicineId = medicineId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}