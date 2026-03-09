package com.floodrescue.backend.citizen.security;

import com.floodrescue.backend.citizen.model.Request;
import com.floodrescue.backend.citizen.repository.RequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("requestSecurity")
@RequiredArgsConstructor
public class RequestSecurity {

    private final RequestRepository requestRepository;

    public boolean isOwner(Integer requestId, String email) {
        if (requestId == null || email == null || email.isBlank()) {
            return false;
        }
        return requestRepository.findById(requestId)
                .map(Request::getUser)
                .map(user -> user.getEmail() != null && user.getEmail().equalsIgnoreCase(email))
                .orElse(false);
    }
}

