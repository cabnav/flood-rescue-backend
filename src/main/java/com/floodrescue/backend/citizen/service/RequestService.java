package com.floodrescue.backend.citizen.service;

import com.floodrescue.backend.citizen.dto.CreateRequestRequest;
import com.floodrescue.backend.citizen.dto.ClassifyRequestRequest;
import com.floodrescue.backend.citizen.dto.RequestDetailResponse;
import com.floodrescue.backend.citizen.model.Request;

import java.util.List;

public interface RequestService {
    RequestDetailResponse createRescue(CreateRequestRequest request);
    RequestDetailResponse createRelief(CreateRequestRequest request);
    RequestDetailResponse classifyRequest(Integer id, ClassifyRequestRequest request);
    RequestDetailResponse getRequestById(Integer id);
    List<RequestDetailResponse> getAllRequests();
    List<RequestDetailResponse> getRequestsByUserId(Integer userId);
    RequestDetailResponse updateRequestStatus(Integer id, String status);
    RequestDetailResponse approveRequestStatus(Integer id);
    RequestDetailResponse cancelRequestStatus(Integer id);
}
