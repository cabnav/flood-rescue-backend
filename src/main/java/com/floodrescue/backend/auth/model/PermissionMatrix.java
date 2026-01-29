package com.floodrescue.backend.auth.model;

import java.util.*;

public class PermissionMatrix {

    private static final Map<RoleType, Set<Permission>> ROLE_PERMISSIONS = new HashMap<>();

    static {
        // GUEST - Chỉ xem công khai
        ROLE_PERMISSIONS.put(RoleType.GUEST, Set.of(

        ));

        // CITIZEN - Người dân
        ROLE_PERMISSIONS.put(RoleType.CITIZEN, Set.of(
                Permission.REQUEST_CREATE,
                Permission.REQUEST_VIEW_OWN,
                Permission.REQUEST_UPDATE_OWN
        ));

        // RESCUE_TEAM - Đội cứu hộ
        ROLE_PERMISSIONS.put(RoleType.RESCUE_TEAM, Set.of(
                Permission.MISSION_VIEW_ASSIGNED,
                Permission.MISSION_UPDATE_ASSIGNED,
                Permission.REQUEST_VIEW_ALL,
                Permission.REQUEST_UPDATE_STATUS,
                Permission.TEAM_VIEW_OWN,
                Permission.VIEW_GPS_COORDINATES,
                Permission.VIEW_MEDICAL_INFO,
                Permission.VEHICLE_VIEW
        ));

        // COORDINATOR - Điều phối viên
        ROLE_PERMISSIONS.put(RoleType.COORDINATOR, Set.of(
                Permission.REQUEST_VIEW_ALL,
                Permission.REQUEST_UPDATE_STATUS,
                Permission.MISSION_CREATE,
                Permission.MISSION_VIEW_ALL,
                Permission.MISSION_UPDATE_ALL,
                Permission.MISSION_ASSIGN,
                Permission.TEAM_VIEW_ALL,
                Permission.TEAM_MANAGE,
                Permission.VEHICLE_VIEW,
                Permission.VEHICLE_ASSIGN,
                Permission.WAREHOUSE_VIEW,
                Permission.VIEW_GPS_COORDINATES,
                Permission.VIEW_MEDICAL_INFO,
                Permission.VIEW_PERSONAL_INFO,
                Permission.VIEW_REPORTS,
                Permission.SEND_NOTIFICATIONS
        ));

        // MANAGER - Quản lý tài nguyên
        ROLE_PERMISSIONS.put(RoleType.MANAGER, Set.of(
                Permission.VEHICLE_VIEW,
                Permission.VEHICLE_CREATE,
                Permission.VEHICLE_UPDATE,
                Permission.VEHICLE_DELETE,
                Permission.VEHICLE_ASSIGN,
                Permission.WAREHOUSE_VIEW,
                Permission.WAREHOUSE_CREATE,
                Permission.WAREHOUSE_UPDATE,
                Permission.WAREHOUSE_DELETE,
                Permission.WAREHOUSE_MANAGE_INVENTORY,
                Permission.MISSION_VIEW_ALL,
                Permission.TEAM_VIEW_ALL,
                Permission.VIEW_REPORTS
        ));

        // ADMIN - Quản trị viên (tất cả quyền)
        ROLE_PERMISSIONS.put(RoleType.ADMIN, EnumSet.allOf(Permission.class));
    }

    public static boolean hasPermission(RoleType role, Permission permission) {
        Set<Permission> permissions = ROLE_PERMISSIONS.get(role);
        return permissions != null && permissions.contains(permission);
    }

    public static Set<Permission> getPermissions(RoleType role) {
        return ROLE_PERMISSIONS.getOrDefault(role, Collections.emptySet());
    }

    public static boolean hasAnyPermission(RoleType role, Permission... permissions) {
        Set<Permission> rolePermissions = getPermissions(role);
        return Arrays.stream(permissions).anyMatch(rolePermissions::contains);
    }
}
