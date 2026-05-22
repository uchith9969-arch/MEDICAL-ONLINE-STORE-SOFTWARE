package com.example.MEDICAL_ONLINE_STORE.keth_user.dto;

import com.example.MEDICAL_ONLINE_STORE.keth_user.model.User;
import com.example.MEDICAL_ONLINE_STORE.keth_user.model.User.Role;
import lombok.Data;


@Data
public class SignupRequest {
    private String name;
    private String email;
    private String password;
    private User.Role role;           // CUSTOMER, SUPPLIER, ADMIN
    private String companyName;  // optional
    private String phoneNumber;
    private String address;
}