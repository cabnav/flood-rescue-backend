package com.floodrescue.backend.auth.service;

import com.floodrescue.backend.auth.dto.LoginRequest;
import com.floodrescue.backend.auth.dto.LoginResponse;
import com.floodrescue.backend.auth.dto.RegisterRequest;
import com.floodrescue.backend.auth.dto.RegisterResponse;
import com.floodrescue.backend.auth.model.Role;
import com.floodrescue.backend.auth.model.User;
import com.floodrescue.backend.auth.repository.RoleRepository;
import com.floodrescue.backend.auth.repository.UserRepository;
import com.floodrescue.backend.common.exception.ResourceNotFoundException;
import com.floodrescue.backend.common.exception.UnauthorizedAccessException;
import com.floodrescue.backend.common.util.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    @Override
    public RegisterResponse register(RegisterRequest request) {
        // TODO: Implement registration logic
        return null;
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        // TODO: Implement login logic
        return null;
    }
}
