package com.floodrescue.backend.admin.service;

import com.floodrescue.backend.admin.dto.UserDetailResponse;
import com.floodrescue.backend.admin.dto.UserStatusUpdateRequest;

import java.util.Collection;
import java.util.List;

public interface UserManagementService {
    List<UserDetailResponse> getAllUsers();
    List<UserDetailResponse> getPendingUsers(Collection<String> roleNames);
    UserDetailResponse getUserById(Integer id);
    UserDetailResponse updateUserStatus(Integer id, UserStatusUpdateRequest request);
    UserDetailResponse approveUser(Integer id);
}
