package com.floodrescue.backend.citizen.service;

import com.floodrescue.backend.citizen.dto.ClassifyRequestRequest;
import com.floodrescue.backend.citizen.dto.CreateRequestRequest;
import com.floodrescue.backend.citizen.dto.RequestDetailResponse;
import com.floodrescue.backend.citizen.model.Request;
import com.floodrescue.backend.citizen.repository.RequestRepository;
import com.floodrescue.backend.auth.model.User;
import com.floodrescue.backend.auth.repository.UserRepository;
import com.floodrescue.backend.admin.repository.FeedbackRepository;
import com.floodrescue.backend.common.exception.BadRequestException;
import com.floodrescue.backend.common.exception.ResourceNotFoundException;
import com.floodrescue.backend.admin.model.Notification;
import com.floodrescue.backend.admin.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;
    private final FeedbackRepository feedbackRepository;

    @Override
    public RequestDetailResponse createRescue(CreateRequestRequest request) {
        // 1. Get UserID from SecurityContextHolder
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw new BadRequestException("Người dùng chưa được xác thực");
        }

        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));

        Integer userId = user.getId();

        // 2. Check Active SOS: Only block if user already has an active RESCUE request
        List<Request.RequestStatus> activeStatuses = Arrays.asList(
                Request.RequestStatus.CREATED,
                Request.RequestStatus.IN_PROGRESS);
        List<Request> activeRequests = requestRepository.findByUserIdAndStatusInAndRequestType(
                userId, activeStatuses, Request.RequestType.RESCUE);

        if (!activeRequests.isEmpty()) {
            throw new BadRequestException("Bạn đã có yêu cầu Cứu hộ đang xử lý");
        }

        // 3. Create new Request with default values
        Request newRequest = new Request();
        newRequest.setUser(user);
        newRequest.setPhone(request.getPhone() != null ? request.getPhone() : user.getPhoneNumber());

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

    @Override
    public RequestDetailResponse createRelief(CreateRequestRequest request) {
        // 1. Get UserID from SecurityContextHolder
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw new BadRequestException("Người dùng chưa được xác thực");
        }

        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));

        Integer userId = user.getId();

        // 2. Check Active SOS: Only block if user already has an active RELIEF request
        List<Request.RequestStatus> activeStatuses = Arrays.asList(
                Request.RequestStatus.CREATED,
                Request.RequestStatus.IN_PROGRESS);
        List<Request> activeRequests = requestRepository.findByUserIdAndStatusInAndRequestType(
                userId, activeStatuses, Request.RequestType.RELIEF);

        if (!activeRequests.isEmpty()) {
            throw new BadRequestException("Bạn đã có yêu cầu Tiếp tế đang xử lý");
        }

        // 3. Create new Request with default values
        Request newRequest = new Request();
        newRequest.setUser(user);
        newRequest.setPhone(request.getPhone() != null ? request.getPhone() : user.getPhoneNumber());

        newRequest.setStatus(Request.RequestStatus.CREATED);
        newRequest.setPriority(request.getPriority() != null ? request.getPriority() : Request.Priority.MEDIUM);
        newRequest.setRequestType(Request.RequestType.RELIEF);

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

    @Override
    @Transactional
    public RequestDetailResponse classifyRequest(Integer id, ClassifyRequestRequest requestBody) {
        Request request = requestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy yêu cầu với id: " + id));

        // Guard: cannot classify completed or cancelled requests
        if (request.getStatus() == Request.RequestStatus.COMPLETED
                || request.getStatus() == Request.RequestStatus.CANCELLED) {
            throw new IllegalStateException("Không thể phân loại yêu cầu đã đóng");
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw new BadRequestException("Người dùng chưa được xác thực");
        }

        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với email: " + email));

        request.setPriority(requestBody.getPriority());
        request.setRequestType(requestBody.getRequestType());
        request.setClassifiedAt(LocalDateTime.now());
        request.setClassifiedBy(user);

        Request saved = requestRepository.save(request);
        return mapToResponse(saved);
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

        boolean feedbackSubmitted = feedbackRepository.existsByRequestIdAndUserId(
                request.getId(),
                request.getUser().getId());
        response.setFeedbackSubmitted(feedbackSubmitted);

        boolean canGiveFeedback = request.getStatus() == Request.RequestStatus.COMPLETED
                && request.getRequestType() != Request.RequestType.OTHER
                && !feedbackSubmitted;
        response.setCanGiveFeedback(canGiveFeedback);

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public RequestDetailResponse getRequestById(Integer id) {
        Request request = requestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy yêu cầu với id: " + id));
        return mapToResponse(request);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RequestDetailResponse> getAllRequests() {
        return requestRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RequestDetailResponse> getRequestsByUserId(Integer userId) {
        return requestRepository.findByUserId(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public RequestDetailResponse updateRequestStatus(Integer id, String status) {
        if (status == null || status.isBlank()) {
            throw new BadRequestException("Trạng thái không được để trống");
        }

        Request.RequestStatus newStatus;
        try {
            newStatus = Request.RequestStatus.valueOf(status.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new BadRequestException("Trạng thái không hợp lệ: " + status);
        }

        Request request = requestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy yêu cầu với id: " + id));

        if (newStatus == request.getStatus()) {
            return mapToResponse(request);
        }

        request.setStatus(newStatus);
        Request savedRequest = requestRepository.save(request);

        Notification notification = new Notification();
        notification.setUser(savedRequest.getUser());
        notification.setMessage("Yêu cầu SOS #" + savedRequest.getId() + " đã được cập nhật trạng thái: " + newStatus);
        notificationRepository.save(notification);
        return mapToResponse(savedRequest);
    }

    @Override
    public RequestDetailResponse approveRequestStatus(Integer id) {
        Request request = requestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy yêu cầu với id: " + id));
        Request.RequestStatus newStatus = Request.RequestStatus.IN_PROGRESS;

        request.setStatus(newStatus);
        Request savedRequest = requestRepository.save(request);

        Notification notification = new Notification();
        notification.setUser(savedRequest.getUser());
        notification.setMessage("Yêu cầu SOS #" + savedRequest.getId() + " đã được cập nhật trạng thái: " + newStatus);
        notificationRepository.save(notification);
        return mapToResponse(savedRequest);
    }

    @Override
    public RequestDetailResponse cancelRequestStatus(Integer id) {
        Request request = requestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy yêu cầu với id: " + id));
        Request.RequestStatus newStatus = Request.RequestStatus.CANCELLED;

        request.setStatus(newStatus);
        Request savedRequest = requestRepository.save(request);

        Notification notification = new Notification();
        notification.setUser(savedRequest.getUser());
        notification.setMessage("Yêu cầu SOS #" + savedRequest.getId() + " đã được cập nhật trạng thái: " + newStatus);
        notificationRepository.save(notification);
        return mapToResponse(savedRequest);
    }

}
