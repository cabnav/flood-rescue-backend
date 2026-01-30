package com.floodrescue.backend.citizen.service;

import com.floodrescue.backend.citizen.dto.CreateRequestRequest;
import com.floodrescue.backend.citizen.dto.RequestDetailResponse;
import com.floodrescue.backend.citizen.model.Request;
import com.floodrescue.backend.citizen.repository.RequestRepository;
import com.floodrescue.backend.auth.model.User;
import com.floodrescue.backend.auth.repository.UserRepository;
import com.floodrescue.backend.common.exception.BadRequestException;
import com.floodrescue.backend.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;

    @Override
    public RequestDetailResponse createRequest(CreateRequestRequest request) {
        // 1. Get UserID from SecurityContextHolder
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw new BadRequestException("User not authenticated");
        }

        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Integer userId = user.getId();

        // 2. Check Active SOS: Check if user has any request with status CREATED or IN_PROGRESS
        List<Request.RequestStatus> activeStatuses = Arrays.asList(
            Request.RequestStatus.CREATED, 
            Request.RequestStatus.IN_PROGRESS
        );
        List<Request> activeRequests = requestRepository.findByUserIdAndStatusIn(userId, activeStatuses);

        if (!activeRequests.isEmpty()) {
            throw new BadRequestException("Bạn đang có một yêu cầu SOS chưa hoàn thành");
        }

        // 3. Create new Request with default values
        Request newRequest = new Request();
        newRequest.setUser(user);
        newRequest.setPhone(request.getPhone() != null ? request.getPhone() : user.getPhoneNumber());
        
        // Default values (priority: dùng từ request nếu có, không thì HIGH để tránh lỗi DB constraint requests_priority_check)
        newRequest.setStatus(Request.RequestStatus.CREATED);
        newRequest.setPriority(request.getPriority() != null ? request.getPriority() : Request.Priority.HIGH);
        newRequest.setRequestType(Request.RequestType.RESCUE);

        // 4. Save latitude, longitude, and description from request
        newRequest.setLatitude(request.getLatitude());
        newRequest.setLongitude(request.getLongitude());
        newRequest.setDescription(request.getDescription());
        
        // Optional fields
        newRequest.setRequestSupplies(request.getRequestSupplies());
        newRequest.setRequestMedia(request.getRequestMedia());

        // 5. Save to database
        Request savedRequest = requestRepository.save(newRequest);

        // 6. Map to response DTO
        return mapToResponse(savedRequest);
    }

    private RequestDetailResponse mapToResponse(Request request) {
        RequestDetailResponse response = new RequestDetailResponse();
        response.setId(request.getId());
        response.setUserId(request.getUser().getId());
        response.setPhone(request.getPhone());
        response.setRequestType(request.getRequestType());
        response.setLatitude(request.getLatitude());
        response.setLongitude(request.getLongitude());
        response.setDescription(request.getDescription());
        response.setPriority(request.getPriority());
        response.setStatus(request.getStatus());
        response.setRequestSupplies(request.getRequestSupplies());
        response.setRequestMedia(request.getRequestMedia());
        response.setCreatedAt(request.getCreatedAt());
        return response;
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
