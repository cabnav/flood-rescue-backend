package com.floodrescue.backend.manager.service;

import com.floodrescue.backend.manager.dto.ReliefDistributionRequest;
import com.floodrescue.backend.manager.dto.ReliefDistributionResponse;

public interface ReliefDistributionService {
    ReliefDistributionResponse createDistribution(ReliefDistributionRequest request);
}

