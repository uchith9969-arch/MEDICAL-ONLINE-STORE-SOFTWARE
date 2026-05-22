package com.example.MEDICAL_ONLINE_STORE.keth_user.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    // NEW: Role field – values: CUSTOMER, SUPPLIER, ADMIN
    @Enumerated(EnumType.STRING)
    private Role role;

    // Optional extra fields for supplier/admin
    private String companyName;   // for supplier
    private String phoneNumber;
    private String address;

    public enum Role {
        CUSTOMER, SUPPLIER, ADMIN
    }
}