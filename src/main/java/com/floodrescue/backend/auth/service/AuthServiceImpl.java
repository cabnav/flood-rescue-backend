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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;

    @Override
    public RegisterResponse register(RegisterRequest request) {
        // TODO: Implement registration logic
        return null;
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        try {
            // Authenticate user with email and password
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            // Load UserDetails from database
            UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());

            // Generate JWT token
            String accessToken = jwtUtils.generateToken(userDetails);

            // Get user entity for additional info
            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));

            // Extract role from authorities
            String role = userDetails.getAuthorities().stream()
                    .findFirst()
                    .map(authority -> authority.getAuthority())
                    .orElse("USER");

            // Build response
            LoginResponse response = new LoginResponse();
            response.setToken(accessToken);
            response.setRefreshToken(null); // TODO: Implement refresh token if needed
            response.setUserId(user.getId());
            response.setEmail(user.getEmail());
            response.setRole(role);

            return response;

        } catch (BadCredentialsException e) {
            throw new UnauthorizedAccessException("Invalid email or password");
        } catch (Exception e) {
            throw new UnauthorizedAccessException("Authentication failed: " + e.getMessage());
        }
    }
}
