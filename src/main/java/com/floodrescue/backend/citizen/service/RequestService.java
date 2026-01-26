package com.floodrescue.backend.citizen.service;

import com.floodrescue.backend.citizen.dto.CreateRequestRequest;
import com.floodrescue.backend.citizen.dto.RequestDetailResponse;
import com.floodrescue.backend.citizen.model.Request;

import java.util.List;

public interface RequestService {
    RequestDetailResponse createRequest(CreateRequestRequest request);
    RequestDetailResponse getRequestById(Integer id);
    List<RequestDetailResponse> getAllRequests();
    List<RequestDetailResponse> getRequestsByUserId(Integer userId);
    RequestDetailResponse updateRequestStatus(Integer id, String status);
}
