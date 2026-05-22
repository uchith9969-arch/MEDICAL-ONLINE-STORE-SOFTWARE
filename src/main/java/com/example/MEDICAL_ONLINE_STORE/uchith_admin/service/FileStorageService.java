package com.example.MEDICAL_ONLINE_STORE.uchith_admin.service;

import org.springframework.stereotype.Service;
import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;

@Service
public class FileStorageService {

    private static final String DATA_DIR = "datafiles/";
    private static final String USERS_FILE = DATA_DIR + "users.txt";
    private static final String ORDERS_FILE = DATA_DIR + "orders.txt";
    private static final String MEDICINES_FILE = DATA_DIR + "medicines.txt";
    private static final String REVIEWS_FILE = DATA_DIR + "reviews.txt";

    public FileStorageService() {
        try { Files.createDirectories(Paths.get(DATA_DIR)); } catch (IOException e) { e.printStackTrace(); }
    }

    public void saveUserRecord(Long id, String name, String email, String role) {
        appendToFile(USERS_FILE, id + "|" + name + "|" + email + "|" + role + "|" + LocalDateTime.now());
    }

    public void saveOrderRecord(Long id, Long userId, Double total, String status) {
        appendToFile(ORDERS_FILE, id + "|" + userId + "|" + total + "|" + status + "|" + LocalDateTime.now());
    }

    public void saveMedicineRecord(Long id, String name, Double price, Integer stock) {
        appendToFile(MEDICINES_FILE, id + "|" + name + "|" + price + "|" + stock + "|" + LocalDateTime.now());
    }

    public void saveReviewRecord(Long id, Long medicineId, Long userId, Integer rating) {
        appendToFile(REVIEWS_FILE, id + "|" + medicineId + "|" + userId + "|" + rating + "|" + LocalDateTime.now());
    }

    public String readAllLogs() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== USERS ===\n").append(readFile(USERS_FILE)).append("\n");
        sb.append("=== ORDERS ===\n").append(readFile(ORDERS_FILE)).append("\n");
        sb.append("=== MEDICINES ===\n").append(readFile(MEDICINES_FILE)).append("\n");
        sb.append("=== REVIEWS ===\n").append(readFile(REVIEWS_FILE));
        return sb.toString();
    }

    private synchronized void appendToFile(String filename, String line) {
        try (FileWriter fw = new FileWriter(filename, true);
             BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write(line); bw.newLine();
        } catch (IOException e) { e.printStackTrace(); }
    }

    private String readFile(String filename) {
        try { return new String(Files.readAllBytes(Paths.get(filename))); }
        catch (IOException e) { return "No data yet."; }
    }
}