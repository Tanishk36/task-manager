package com.taskmanager.service;

import com.taskmanager.dto.DTOs.*;
import com.taskmanager.entity.User;
import com.taskmanager.repository.UserRepository;
import com.taskmanager.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired 
    private UserRepository userRepository;

    @Autowired 
    private PasswordEncoder passwordEncoder;

    @Autowired 
    private AuthenticationManager authenticationManager;

    @Autowired 
    private JwtUtils jwtUtils;

    // ================= SIGNUP =================
    public AuthResponse signup(SignupRequest request) {

        // 🔒 Validation checks
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email is already registered");
        }

        if (userRepository.existsByPhone(request.getPhone())) {
            throw new RuntimeException("Phone number is already registered");
        }

        // 🧱 Build user object
        User user = User.builder()
                .fullName(request.getFullName().trim())
                .email(request.getEmail().toLowerCase().trim())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .role(request.getRole() != null ? request.getRole() : User.Role.MEMBER)
                .build();

        

        // 💾 Save user
        user = userRepository.save(user);

        // 🔐 Generate JWT
        String token = jwtUtils.generateTokenFromEmail(user.getEmail());

        // 📦 Return response
        return AuthResponse.builder()
                .token(token)
                .type("Bearer")
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }

    // ================= LOGIN =================
    public AuthResponse login(LoginRequest request) {

        // 🔐 Authenticate user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail().toLowerCase().trim(),
                        request.getPassword()
                )
        );

        // 🎟 Generate token
        String token = jwtUtils.generateToken(authentication);

        // 👤 Fetch user details
        User user = userRepository.findByEmail(request.getEmail().toLowerCase().trim())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 📦 Return response
        return AuthResponse.builder()
                .token(token)
                .type("Bearer")
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }
}