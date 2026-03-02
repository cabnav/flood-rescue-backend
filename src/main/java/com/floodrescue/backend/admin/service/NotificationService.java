package com.floodrescue.backend.admin.service;

import com.floodrescue.backend.admin.dto.NotificationResponse;
import java.util.List;

public interface NotificationService {
    NotificationResponse create(Integer userId, String message);

    NotificationResponse getById(Integer id);

    List<NotificationResponse> getByUserId(Integer userId);

    List<NotificationResponse> getUnreadByUserId(Integer userId);

    NotificationResponse markAsRead(Integer id);
}