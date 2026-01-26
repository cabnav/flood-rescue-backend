package com.floodrescue.backend.admin.service;

import com.floodrescue.backend.admin.dto.UserDetailResponse;
import com.floodrescue.backend.admin.dto.UserStatusUpdateRequest;
import com.floodrescue.backend.auth.model.User;
import com.floodrescue.backend.auth.repository.UserRepository;
import com.floodrescue.backend.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserManagementServiceImpl implements UserManagementService {

    private final UserRepository userRepository;

    @Override
    public List<UserDetailResponse> getAllUsers() {
        // TODO: Implement get all users logic
        return null;
    }

    @Override
    public UserDetailResponse getUserById(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        // TODO: Map to response DTO
        return null;
    }

    @Override
    public UserDetailResponse updateUserStatus(Integer id, UserStatusUpdateRequest request) {
        // TODO: Implement update user status logic
        return null;
    }

    @Override
    public UserDetailResponse approveUser(Integer id) {
        // TODO: Implement approve user logic
        return null;
    }
}
