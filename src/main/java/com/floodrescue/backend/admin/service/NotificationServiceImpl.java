package com.floodrescue.backend.admin.service;

import com.floodrescue.backend.admin.dto.NotificationResponse;

import java.util.List;

public class NotificationServiceImpl implements  NotificationService{

    @Override
    public NotificationResponse create(Integer userId, String message) {
        return null;
    }

    @Override
    public NotificationResponse getById(Integer id) {
        return null;
    }

    @Override
    public List<NotificationResponse> getByUserId(Integer userId) {
        return List.of();
    }

    @Override
    public List<NotificationResponse> getUnreadByUserId(Integer userId) {
        return List.of();
    }

    @Override
    public NotificationResponse markAsRead(Integer id) {
        return null;
    }
}
