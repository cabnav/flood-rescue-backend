package com.floodrescue.backend.auth.controller;

import com.floodrescue.backend.auth.dto.LoginRequest;
import com.floodrescue.backend.auth.dto.LoginResponse;
import com.floodrescue.backend.auth.dto.RegisterRequest;
import com.floodrescue.backend.auth.dto.RegisterResponse;
import com.floodrescue.backend.auth.dto.UpdateProfileRequest;
import com.floodrescue.backend.auth.dto.UserProfileResponse;
import com.floodrescue.backend.auth.service.AuthService;
import com.floodrescue.backend.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisterResponse>> register(@Valid @RequestBody RegisterRequest request) {
        RegisterResponse response = authService.register(request);
        return ResponseEntity.ok(ApiResponse.success("User registered successfully", response));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }

    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getProfile() {
        // Lấy email từ SecurityContextHolder
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        UserProfileResponse response = authService.getProfile(email);
        return ResponseEntity.ok(ApiResponse.success("Profile retrieved successfully", response));
    }

    @PutMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        // Lấy email từ SecurityContextHolder
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        UserProfileResponse response = authService.updateProfile(email, request);
        return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", response));
    }
}
