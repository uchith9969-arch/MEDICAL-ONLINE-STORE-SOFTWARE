package com.example.MEDICAL_ONLINE_STORE.samod_medicine.service;

import com.example.MEDICAL_ONLINE_STORE.keth_user.model.User;
import com.example.MEDICAL_ONLINE_STORE.keth_user.repository.UserRepository;
import com.example.MEDICAL_ONLINE_STORE.samod_medicine.dto.MedicineRequestDTO;
import com.example.MEDICAL_ONLINE_STORE.samod_medicine.dto.MedicineResponseDTO;
import com.example.MEDICAL_ONLINE_STORE.samod_medicine.exception.MedicineNotFoundException;
import com.example.MEDICAL_ONLINE_STORE.samod_medicine.model.Medicine;
import com.example.MEDICAL_ONLINE_STORE.samod_medicine.model.MedicineCategory;
import com.example.MEDICAL_ONLINE_STORE.samod_medicine.repository.MedicineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MedicineService {

    @Autowired
    private MedicineRepository medicineRepository;

    @Autowired
    private UserRepository userRepository;

    // Find medicine or throw checked exception
    public Medicine findMedicineById(Long id) throws MedicineNotFoundException {
        return medicineRepository.findById(id)
                .orElseThrow(() -> new MedicineNotFoundException("Medicine not found with ID: " + id));
    }

    // Map Medicine entity ---> MedicineResponseDTO
    private MedicineResponseDTO toResponseDTO(Medicine medicine) {
        return new MedicineResponseDTO(
                medicine.getId(),
                medicine.getName(),
                medicine.getDescription(),
                medicine.getPrice(),
                medicine.getCategory(),
                medicine.getStockQuantity()
                
        );
    }

    // Create medicine
    public MedicineResponseDTO createMedicine(MedicineRequestDTO requestDTO) {
        Medicine medicine = new Medicine();
        medicine.setName(requestDTO.getName());
        medicine.setDescription(requestDTO.getDescription());
        medicine.setPrice(requestDTO.getPrice());
        medicine.setCategory(requestDTO.getCategory());
        medicine.setStockQuantity(requestDTO.getStockQuantity());
        return toResponseDTO(medicineRepository.save(medicine));
    }

    // Get all medicines
    public List<MedicineResponseDTO> getAllMedicines() {
        return medicineRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    // Get medicine by ID
    public MedicineResponseDTO getMedicineById(Long id) throws MedicineNotFoundException {
        return toResponseDTO(findMedicineById(id));
    }

    // Search medicines by name
    public List<MedicineResponseDTO> searchByName(String name) {
        return medicineRepository.findByNameContainingIgnoreCase(name)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    // Get medicines by category
    public List<MedicineResponseDTO> getMedicinesByCategory(MedicineCategory category) {
        return medicineRepository.findByCategory(category)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }


    // Get low stock medicines (at or below threshold)
    public List<MedicineResponseDTO> getLowStockMedicines(Integer threshold) {
        return medicineRepository.findByStockQuantityLessThanEqual(threshold)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    // Update medicine
    public MedicineResponseDTO updateMedicine(Long id, MedicineRequestDTO requestDTO) throws MedicineNotFoundException {
        Medicine medicine = findMedicineById(id);
        medicine.setName(requestDTO.getName());
        medicine.setDescription(requestDTO.getDescription());
        medicine.setPrice(requestDTO.getPrice());
        medicine.setCategory(requestDTO.getCategory());
        medicine.setStockQuantity(requestDTO.getStockQuantity());
        return toResponseDTO(medicineRepository.save(medicine));
    }

    // Delete medicine
    public void deleteMedicine(Long id) throws MedicineNotFoundException {
        Medicine medicine = findMedicineById(id);
        medicineRepository.delete(medicine);
    }

    // Restock medicine (add stock)
    public MedicineResponseDTO restockMedicine(Long id, Integer quantity) throws MedicineNotFoundException {
        if (quantity == null || quantity <= 0) {
            throw new RuntimeException("Restock quantity must be greater than 0");
        }
        Medicine medicine = findMedicineById(id);
        medicine.setStockQuantity(medicine.getStockQuantity() + quantity);
        return toResponseDTO(medicineRepository.save(medicine));
    }

    // Deduct stock when payment is completed
    public void deductStock(Long medicineId, Integer quantity) throws MedicineNotFoundException {
        Medicine medicine = findMedicineById(medicineId);

        if (medicine.getStockQuantity() < quantity) {
            throw new RuntimeException("Insufficient stock for medicine: " + medicine.getName()
                    + ". Available: " + medicine.getStockQuantity() + ", Requested: " + quantity);
        }

        medicine.setStockQuantity(medicine.getStockQuantity() - quantity);
        medicineRepository.save(medicine);
    }

    // Restore stock on refund
    public void restoreStock(Long medicineId, Integer quantity) throws MedicineNotFoundException {
        Medicine medicine = findMedicineById(medicineId);
        medicine.setStockQuantity(medicine.getStockQuantity() + quantity);
        medicineRepository.save(medicine);
    }

    public List<Medicine> getMedicinesBySupplierId(Long supplierId) {
    return medicineRepository.findBySupplierId(supplierId);
    }


    //Create medicine for supplier
    public MedicineResponseDTO createMedicineForSupplier(MedicineRequestDTO dto, Long supplierId) {
    Medicine medicine = new Medicine();
    medicine.setName(dto.getName());
    medicine.setDescription(dto.getDescription());
    medicine.setPrice(dto.getPrice());
    medicine.setCategory(dto.getCategory());
    medicine.setStockQuantity(dto.getStockQuantity());
    
    

    // Fetch supplier user
    User supplier = userRepository.findById(supplierId).orElseThrow(() -> new RuntimeException("Supplier not found"));
    medicine.setSupplier(supplier);
    return toResponseDTO(medicineRepository.save(medicine));
    }

    public int getTotalMedicineStock() {
        return medicineRepository.findAll()
                .stream()
                .mapToInt(Medicine::getStockQuantity)
                .sum();
    }





}