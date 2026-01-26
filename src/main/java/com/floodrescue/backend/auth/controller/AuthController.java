package com.floodrescue.backend.auth.controller;

import com.floodrescue.backend.auth.dto.LoginRequest;
import com.floodrescue.backend.auth.dto.LoginResponse;
import com.floodrescue.backend.auth.dto.RegisterRequest;
import com.floodrescue.backend.auth.dto.RegisterResponse;
import com.floodrescue.backend.auth.service.AuthService;
import com.floodrescue.backend.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisterResponse>> register(@RequestBody RegisterRequest request) {
        RegisterResponse response = authService.register(request);
        return ResponseEntity.ok(ApiResponse.success("User registered successfully", response));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }
}
