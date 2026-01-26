package com.floodrescue.backend.auth.service;

import com.floodrescue.backend.auth.dto.LoginRequest;
import com.floodrescue.backend.auth.dto.LoginResponse;
import com.floodrescue.backend.auth.dto.RegisterRequest;
import com.floodrescue.backend.auth.dto.RegisterResponse;

public interface AuthService {
    RegisterResponse register(RegisterRequest request);
    LoginResponse login(LoginRequest request);
}
