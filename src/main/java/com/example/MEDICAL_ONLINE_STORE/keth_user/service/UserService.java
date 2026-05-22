package com.example.MEDICAL_ONLINE_STORE.keth_user.service;

import com.example.MEDICAL_ONLINE_STORE.keth_user.dto.LoginRequest;
import com.example.MEDICAL_ONLINE_STORE.keth_user.dto.SignupRequest;
import com.example.MEDICAL_ONLINE_STORE.keth_user.model.User;
import com.example.MEDICAL_ONLINE_STORE.keth_user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // FIX: Single shared BCryptPasswordEncoder instance (thread-safe, reusable)
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private static final String RESERVED_ADMIN_EMAIL = "Uchith9969@gmail.com";

    public String registerUser(SignupRequest signupRequest) {
        if (userRepository.findByEmail(signupRequest.getEmail()).isPresent()) {
            return "Email already exists";
        }

        // Block anyone from registering as ADMIN
        if (signupRequest.getRole() == User.Role.ADMIN) {
            return "Admin registration is not allowed";
        }

        User user = new User();
        user.setName(signupRequest.getName());
        user.setEmail(signupRequest.getEmail());
        // FIX: Hash the password before saving — never store plain text
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        user.setRole(signupRequest.getRole());
        user.setCompanyName(signupRequest.getCompanyName());
        user.setPhoneNumber(signupRequest.getPhoneNumber());
        user.setAddress(signupRequest.getAddress());
        userRepository.save(user);
        return "User registered successfully";
    }

    public String loginUser(LoginRequest loginRequest) {
        Optional<User> userOpt = userRepository.findByEmail(loginRequest.getEmail());
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            // FIX: Use BCrypt matches() to compare raw password against stored hash
            if (passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                return "Login successful|" + user.getRole().name() + "|" + user.getId();
            } else {
                return "Invalid password";
            }
        } else {
            return "User not found";
        }
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    public User updateUser(User user) {
        return userRepository.save(user);
    }
}