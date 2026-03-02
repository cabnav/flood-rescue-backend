package com.floodrescue.backend.admin.service;

import com.floodrescue.backend.admin.dto.UserDetailResponse;
import com.floodrescue.backend.admin.dto.UserStatusUpdateRequest;
import com.floodrescue.backend.auth.model.User;
import com.floodrescue.backend.auth.repository.UserRepository;
import com.floodrescue.backend.common.exception.BadRequestException;
import com.floodrescue.backend.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserManagementServiceImpl implements UserManagementService {

    private final UserRepository userRepository;

    @Override
    public List<UserDetailResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserDetailResponse> getPendingUsers(Collection<String> roleNames) {
        Collection<String> roles = (roleNames == null || roleNames.isEmpty())
                ? Set.of("RESCUE_TEAM", "RESCUE_COORDINATOR")
                : roleNames;

        return userRepository.findByIsActiveFalseAndRole_NameIn(roles).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public UserDetailResponse getUserById(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return mapToResponse(user);
    }

    @Override
    public UserDetailResponse updateUserStatus(Integer id, UserStatusUpdateRequest request) {
        if (request == null || request.getIsActive() == null) {
            throw new BadRequestException("isActive is required");
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        // Manual block/unblock:
        // - Block: set isActive=false and clear lock fields to avoid auto-unlock logic
        // - Unblock: set isActive=true and reset failedAttempt/lockTime
        user.setIsActive(request.getIsActive());
        user.setFailedAttempt(0);
        user.setLockTime(null);

        User saved = userRepository.save(user);
        return mapToResponse(saved);
    }

    @Override
    public UserDetailResponse approveUser(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        String roleName = user.getRole() != null ? user.getRole().getName() : null;
        if (roleName == null) {
            throw new BadRequestException("User has no role");
        }

        // AD-01: only approve Rescue Team / Coordinator registrations
        if (!"RESCUE_TEAM".equalsIgnoreCase(roleName) && !"RESCUE_COORDINATOR".equalsIgnoreCase(roleName)) {
            throw new BadRequestException("Only RESCUE_TEAM or RESCUE_COORDINATOR accounts require approval");
        }

        user.setIsActive(true);
        user.setFailedAttempt(0);
        user.setLockTime(null);

        User saved = userRepository.save(user);
        return mapToResponse(saved);
    }

    private UserDetailResponse mapToResponse(User user) {
        UserDetailResponse response = new UserDetailResponse();
        response.setId(user.getId());
        response.setFullName(user.getFullName());
        response.setEmail(user.getEmail());
        response.setPhoneNumber(user.getPhoneNumber());
        response.setRole(user.getRole() != null ? user.getRole().getName() : null);
        response.setIsActive(user.getIsActive());
        response.setCreatedAt(user.getCreatedAt());
        return response;
    }
}
