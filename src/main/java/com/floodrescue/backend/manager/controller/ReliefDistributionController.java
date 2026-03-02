package com.floodrescue.backend.manager.controller;

import com.floodrescue.backend.common.dto.ApiResponse;
import com.floodrescue.backend.manager.dto.ReliefDistributionRequest;
import com.floodrescue.backend.manager.dto.ReliefDistributionResponse;
import com.floodrescue.backend.manager.service.ReliefDistributionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/relief-distributions")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
public class ReliefDistributionController {

    private final ReliefDistributionService reliefDistributionService;

    @PostMapping
    public ResponseEntity<ApiResponse<ReliefDistributionResponse>> createDistribution(
            @Valid @RequestBody ReliefDistributionRequest request) {
        ReliefDistributionResponse response = reliefDistributionService.createDistribution(request);
        return ResponseEntity.ok(ApiResponse.success("Ghi nhận phân phối thành công", response));
    }
}

