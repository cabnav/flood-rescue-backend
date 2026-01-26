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
import com.floodrescue.backend.common.util.ApplicationConstants;
import com.floodrescue.backend.common.util.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

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
        // 1. Unique Check: Email
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        // 2. Unique Check: Phone Number
        if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new RuntimeException("Phone number already exists");
        }

        // 3. Find Role from RoleRepository
        Role role = roleRepository.findByName(request.getRoleName())
                .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + request.getRoleName()));

        // 4. Determine isActive status based on roleName
        Boolean isActive;
        String roleName = request.getRoleName().toUpperCase();
        if ("CITIZEN".equals(roleName)) {
            isActive = true;
        } else if ("RESCUE_TEAM".equals(roleName) || "RESCUE_COORDINATOR".equals(roleName)) {
            isActive = false; // Pending Admin approval
        } else {
            // Default: other roles are active by default
            isActive = true;
        }

        // 5. Encode password
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // 6. Create new User
        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setPasswordHash(encodedPassword);
        user.setRole(role);
        user.setIsActive(isActive);
        user.setCreatedAt(LocalDateTime.now());

        // 7. Save user to database
        User savedUser = userRepository.save(user);

        // 8. Build and return response
        RegisterResponse response = new RegisterResponse();
        response.setUserId(savedUser.getId());
        response.setEmail(savedUser.getEmail());
        
        if (isActive) {
            response.setMessage("User registered successfully");
        } else {
            response.setMessage("User registered successfully. Account pending admin approval.");
        }

        return response;
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        // Get user from database
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UnauthorizedAccessException("Invalid email or password"));

        // Check if account is locked
        if (user.getLockTime() != null && isAccountLocked(user)) {
            throw new RuntimeException("Account is locked. Please try again later.");
        }

        try {
            // Authenticate user with email and password
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            // Login successful - reset failed attempts
            resetFailedAttempts(user);

            // Load UserDetails from database
            UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());

            // Generate JWT token
            String accessToken = jwtUtils.generateToken(userDetails);

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
            // Login failed - increase failed attempts
            increaseFailedAttempts(user);
            throw new UnauthorizedAccessException("Invalid email or password");
        } catch (Exception e) {
            throw new UnauthorizedAccessException("Authentication failed: " + e.getMessage());
        }
    }

    /**
     * Check if account is still locked based on lockTime
     */
    private boolean isAccountLocked(User user) {
        if (user.getLockTime() == null) {
            return false;
        }

        LocalDateTime lockTime = user.getLockTime();
        LocalDateTime unlockTime = lockTime.plusMinutes(ApplicationConstants.LOCK_TIME_DURATION_MINUTES);
        LocalDateTime now = LocalDateTime.now();

        // If lock time has expired, unlock the account
        if (now.isAfter(unlockTime)) {
            user.setLockTime(null);
            user.setFailedAttempt(0);
            userRepository.save(user);
            return false;
        }

        return true;
    }

    /**
     * Reset failed attempts counter when login is successful
     */
    private void resetFailedAttempts(User user) {
        user.setFailedAttempt(0);
        user.setLockTime(null);
        userRepository.save(user);
    }

    /**
     * Increase failed attempts and lock account if exceeds MAX_FAILED_ATTEMPTS
     */
    private void increaseFailedAttempts(User user) {
        int failedAttempts = user.getFailedAttempt() != null ? user.getFailedAttempt() : 0;
        failedAttempts++;

        user.setFailedAttempt(failedAttempts);

        // If exceeded max attempts, lock the account
        if (failedAttempts >= ApplicationConstants.MAX_FAILED_ATTEMPTS) {
            user.setLockTime(LocalDateTime.now());
            user.setIsActive(false);
        }

        userRepository.save(user);
    }
}
