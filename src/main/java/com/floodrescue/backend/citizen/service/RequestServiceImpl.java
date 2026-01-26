package com.floodrescue.backend.citizen.service;

import com.floodrescue.backend.citizen.dto.CreateRequestRequest;
import com.floodrescue.backend.citizen.dto.RequestDetailResponse;
import com.floodrescue.backend.citizen.model.Request;
import com.floodrescue.backend.citizen.repository.RequestRepository;
import com.floodrescue.backend.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;

    @Override
    public RequestDetailResponse createRequest(CreateRequestRequest request) {
        // TODO: Implement create request logic
        return null;
    }

    @Override
    public RequestDetailResponse getRequestById(Integer id) {
        Request request = requestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found with id: " + id));
        // TODO: Map to response DTO
        return null;
    }

    @Override
    public List<RequestDetailResponse> getAllRequests() {
        // TODO: Implement get all requests logic
        return null;
    }

    @Override
    public List<RequestDetailResponse> getRequestsByUserId(Integer userId) {
        // TODO: Implement get requests by user logic
        return null;
    }

    @Override
    public RequestDetailResponse updateRequestStatus(Integer id, String status) {
        // TODO: Implement update status logic
        return null;
    }
}
