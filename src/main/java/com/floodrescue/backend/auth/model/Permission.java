package com.floodrescue.backend.auth.model;

public enum Permission {
    // Request permissions
    REQUEST_CREATE,           // Tạo yêu cầu cứu hộ
    REQUEST_VIEW_OWN,         // Xem yêu cầu của mình
    REQUEST_VIEW_ALL,         // Xem tất cả yêu cầu
    REQUEST_UPDATE_OWN,       // Cập nhật yêu cầu của mình
    REQUEST_UPDATE_STATUS,    // Cập nhật trạng thái yêu cầu
    REQUEST_DELETE,           // Xóa yêu cầu

    // Mission permissions
    MISSION_CREATE,           // Tạo nhiệm vụ
    MISSION_VIEW_ASSIGNED,    // Xem nhiệm vụ được giao
    MISSION_VIEW_ALL,         // Xem tất cả nhiệm vụ
    MISSION_UPDATE_ASSIGNED,  // Cập nhật nhiệm vụ được giao
    MISSION_UPDATE_ALL,       // Cập nhật bất kỳ nhiệm vụ
    MISSION_ASSIGN,           // Phân công nhiệm vụ
    MISSION_DELETE,           // Xóa nhiệm vụ

    // Team permissions
    TEAM_VIEW_OWN,            // Xem thông tin team của mình
    TEAM_VIEW_ALL,            // Xem tất cả teams
    TEAM_MANAGE,              // Quản lý teams

    // Vehicle permissions
    VEHICLE_VIEW,             // Xem danh sách xe
    VEHICLE_CREATE,           // Tạo xe mới
    VEHICLE_UPDATE,           // Cập nhật thông tin xe
    VEHICLE_DELETE,           // Xóa xe
    VEHICLE_ASSIGN,           // Phân xe cho nhiệm vụ

    // Warehouse permissions
    WAREHOUSE_VIEW,           // Xem kho
    WAREHOUSE_CREATE,         // Tạo kho
    WAREHOUSE_UPDATE,         // Cập nhật kho
    WAREHOUSE_DELETE,         // Xóa kho
    WAREHOUSE_MANAGE_INVENTORY, // Quản lý tồn kho

    // Sensitive data permissions
    VIEW_GPS_COORDINATES,     // Xem tọa độ GPS
    VIEW_MEDICAL_INFO,        // Xem thông tin y tế
    VIEW_PERSONAL_INFO,       // Xem thông tin cá nhân

    // User management
    USER_VIEW_ALL,            // Xem tất cả users
    USER_CREATE,              // Tạo user
    USER_UPDATE,              // Cập nhật user
    USER_DELETE,              // Xóa user
    USER_ASSIGN_ROLE,         // Gán role

    // System
    SYSTEM_SETTINGS,          // Cài đặt hệ thống
    VIEW_REPORTS,             // Xem báo cáo
    SEND_NOTIFICATIONS        // Gửi thông báo
}
