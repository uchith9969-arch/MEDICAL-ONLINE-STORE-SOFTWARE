package com.example.MEDICAL_ONLINE_STORE.keth_user.controller;

import com.example.MEDICAL_ONLINE_STORE.keth_user.dto.LoginRequest;
import com.example.MEDICAL_ONLINE_STORE.keth_user.dto.SignupRequest;
import com.example.MEDICAL_ONLINE_STORE.keth_user.model.User;
import com.example.MEDICAL_ONLINE_STORE.keth_user.service.UserService;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody SignupRequest signupRequest) {
        String result = userService.registerUser(signupRequest);
        if (result.equals("User registered successfully")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest loginRequest) {
        String result = userService.loginUser(loginRequest);

        Map<String, Object> response = new HashMap<>();

        // result format: "Login successful|ROLE|ID"
        if (result.startsWith("Login successful")) {
            String[] parts = result.split("\\|");
            String role = parts.length > 1 ? parts[1] : "CUSTOMER";
            Long userId = parts.length > 2 ? Long.parseLong(parts[2]) : null;

            response.put("message", "Login successful");
            response.put("role", role);
            response.put("userId", userId);
            return ResponseEntity.ok(response);
        } else {
            response.put("message", result);
            return ResponseEntity.badRequest().body(response);
        }
    }
}