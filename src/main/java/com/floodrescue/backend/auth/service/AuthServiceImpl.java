package com.floodrescue.backend.auth.service;

import com.floodrescue.backend.auth.dto.LoginRequest;
import com.floodrescue.backend.auth.dto.LoginResponse;
import com.floodrescue.backend.auth.dto.RegisterRequest;
import com.floodrescue.backend.auth.dto.RegisterResponse;
import com.floodrescue.backend.auth.dto.UpdateProfileRequest;
import com.floodrescue.backend.auth.dto.UserProfileResponse;
import com.floodrescue.backend.auth.model.Role;
import com.floodrescue.backend.auth.model.User;
import com.floodrescue.backend.auth.repository.RoleRepository;
import com.floodrescue.backend.auth.repository.UserRepository;
import com.floodrescue.backend.common.exception.BadRequestException;
import com.floodrescue.backend.common.exception.ResourceNotFoundException;
import com.floodrescue.backend.common.exception.UnauthorizedAccessException;
import com.floodrescue.backend.common.util.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

    // Constants for account locking
    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final int LOCK_TIME_DURATION_MINUTES = 30;
    private static final String DEFAULT_REGISTER_ROLE = "CITIZEN";

    @Override
    public RegisterResponse register(RegisterRequest request) {
        // 1. Check trùng email
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already exists");
        }

        // 2. Check trùng SĐT
        if (request.getPhoneNumber() != null && userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new BadRequestException("Phone number already exists");
        }

        // 3. Role đăng ký luôn mặc định là CITIZEN (không cho client tự truyền role)
        Role role = roleRepository.findByName(DEFAULT_REGISTER_ROLE)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + DEFAULT_REGISTER_ROLE));

        // 4. Mã hóa password bằng BCryptPasswordEncoder
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // 5. Gán Role và set isActive
        // Business Rule 1.1: CITIZEN auto-active
        Boolean isActive = true;

        // 6. Tạo User mới
        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setPasswordHash(encodedPassword);
        user.setRole(role);
        user.setIsActive(isActive);
        user.setCreatedAt(LocalDateTime.now());

        // 7. Lưu vào database
        User savedUser = userRepository.save(user);

        // 8. Trả về RegisterResponse
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
    @Transactional
    public LoginResponse login(LoginRequest request) {
        // 1. Tìm user từ database
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UnauthorizedAccessException("Invalid email or password"));

        // 2. Kiểm tra xem tài khoản có đang bị khóa không
        if (user.getLockTime() != null) {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime unlockTime = user.getLockTime().plusMinutes(LOCK_TIME_DURATION_MINUTES);
            
            if (now.isBefore(unlockTime)) {
                // Tài khoản vẫn còn bị khóa
                throw new UnauthorizedAccessException("Account is locked. Please try again later.");
            } else {
                // Hết hạn khóa do đăng nhập sai -> Mở khóa tự động (không ảnh hưởng pending approval)
                // Chỉ auto-reactivate nếu đây đúng là lock do failed attempts.
                if (user.getFailedAttempt() != null && user.getFailedAttempt() >= MAX_FAILED_ATTEMPTS) {
                    user.setFailedAttempt(0);
                    user.setLockTime(null);
                    user.setIsActive(true);
                    userRepository.save(user);
                } else {
                    throw new UnauthorizedAccessException("Account is locked. Please contact admin.");
                }
            }
        }

        // Nếu tài khoản đang bị inactive (pending approval / blocked) thì không cho login
            if (Boolean.FALSE.equals(user.getIsActive())) {
            throw new UnauthorizedAccessException("Account is not active. Please contact admin.");
        }

        try {
            // 3. Dùng authenticationManager để authenticate
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            // 4. Nếu đúng mật khẩu: Reset failedAttempt và generate token
            user.setFailedAttempt(0);
            userRepository.save(user);

            // 5. Load UserDetails để lấy role
            UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
            String role = userDetails.getAuthorities().stream()
                    .findFirst()
                    .map(auth -> auth.getAuthority())
                    .orElse("");
            // Trả role cho client dạng "ADMIN/CITIZEN/..." thay vì "ROLE_ADMIN"
            if (role.startsWith("ROLE_")) {
                role = role.substring("ROLE_".length());
            }

            // 6. Generate JWT Token
            // role claim (nếu dùng) sẽ là ROLE_*
            String token = jwtUtils.generateToken(userDetails.getUsername(), "ROLE_" + role);

            // 7. Trả về LoginResponse
            LoginResponse response = new LoginResponse();
            response.setToken(token);
            response.setUserId(user.getId());
            response.setEmail(user.getEmail());
            response.setRole(role);
            // refreshToken có thể để null hoặc implement sau
            response.setRefreshToken(null);

            return response;

        } catch (DisabledException e) {
            throw new UnauthorizedAccessException("Account is not active. Please contact admin.");
        } catch (BadCredentialsException e) {
            // 8. Nếu sai mật khẩu: Tăng failedAttempt
            int newFailedAttempt = (user.getFailedAttempt() == null ? 0 : user.getFailedAttempt()) + 1;
            user.setFailedAttempt(newFailedAttempt);

            // 9. Nếu failedAttempt >= 5 -> Khóa tài khoản
            if (newFailedAttempt >= MAX_FAILED_ATTEMPTS) {
                user.setLockTime(LocalDateTime.now());
                user.setIsActive(false);
                userRepository.save(user);
                throw new UnauthorizedAccessException("Account locked due to too many failed login attempts. Please try again after 30 minutes.");
            }

            userRepository.save(user);
            throw new UnauthorizedAccessException("Invalid email or password");
        }
    }

    @Override
    public UserProfileResponse getProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        UserProfileResponse response = new UserProfileResponse();
        response.setId(user.getId());
        response.setEmail(user.getEmail());
        response.setFullName(user.getFullName());
        response.setPhoneNumber(user.getPhoneNumber());
        response.setRole(user.getRole() != null ? user.getRole().getName() : null);
        response.setAvatar(user.getAvatar64());

        return response;
    }

    @Override
    @Transactional
    public UserProfileResponse updateProfile(String email, UpdateProfileRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        // Chỉ cho phép update fullName và phoneNumber
        // Không cho phép update Role (theo Business Rule AUTH-03)

        // Update fullName nếu có
        if (request.getFullName() != null && !request.getFullName().trim().isEmpty()) {
            user.setFullName(request.getFullName());
        }

        // Update phoneNumber nếu có và check trùng (nếu khác với số hiện tại)
        if (request.getPhoneNumber() != null && !request.getPhoneNumber().trim().isEmpty()) {
            // Nếu phoneNumber mới khác với phoneNumber hiện tại, check trùng
            if (!request.getPhoneNumber().equals(user.getPhoneNumber())) {
                if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
                    throw new BadRequestException("Phone number already exists");
                }
            }
            user.setPhoneNumber(request.getPhoneNumber());
        }

        // Lưu vào database
        User savedUser = userRepository.save(user);

        // Trả về UserProfileResponse
        UserProfileResponse response = new UserProfileResponse();
        response.setId(savedUser.getId());
        response.setEmail(savedUser.getEmail());
        response.setFullName(savedUser.getFullName());
        response.setPhoneNumber(savedUser.getPhoneNumber());
        response.setRole(savedUser.getRole() != null ? savedUser.getRole().getName() : null);
        response.setAvatar(savedUser.getAvatar64());

        return response;
    }
}
