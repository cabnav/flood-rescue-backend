package com.floodrescue.backend.common.interceptor;

import com.floodrescue.backend.common.annotation.RequireRole;
import com.floodrescue.backend.common.exception.ForbiddenException;
import com.floodrescue.backend.common.exception.UnauthorizedAccessException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;

@Component
public class RoleAuthorizationInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;

        // Kiểm tra annotation ở method level
        RequireRole requireRole = handlerMethod.getMethodAnnotation(RequireRole.class);

        // Nếu không có ở method, kiểm tra ở class level
        if (requireRole == null) {
            requireRole = handlerMethod.getBeanType().getAnnotation(RequireRole.class);
        }

        // Nếu không có annotation, cho phép truy cập
        if (requireRole == null) {
            return true;
        }

        // Lấy role từ request attribute (đã được set bởi JwtAuthenticationFilter)
        String userRole = (String) request.getAttribute("userRole");

        if (userRole == null) {
            throw new UnauthorizedAccessException("Authentication required");
        }

        // Kiểm tra role có match không
        String[] allowedRoles = requireRole.value();
        boolean hasPermission = Arrays.asList(allowedRoles).contains(userRole);

        if (!hasPermission) {
            throw new ForbiddenException(
                    "Access denied. Required roles: " + Arrays.toString(allowedRoles) +
                            ", but user has role: " + userRole
            );
        }

        return true;
    }
}