package com.floodrescue.backend.manager.service;

import com.floodrescue.backend.auth.model.User;
import com.floodrescue.backend.auth.repository.UserRepository;
import com.floodrescue.backend.common.exception.BadRequestException;
import com.floodrescue.backend.common.exception.InsufficientInventoryException;
import com.floodrescue.backend.common.exception.ResourceNotFoundException;
import com.floodrescue.backend.manager.dto.ReliefDistributionRequest;
import com.floodrescue.backend.manager.dto.ReliefDistributionResponse;
import com.floodrescue.backend.manager.model.Inventory;
import com.floodrescue.backend.manager.model.ReliefDistribution;
import com.floodrescue.backend.manager.repository.InventoryRepository;
import com.floodrescue.backend.manager.repository.ReliefDistributionRepository;
import com.floodrescue.backend.rescue.model.Mission;
import com.floodrescue.backend.rescue.repository.MissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ReliefDistributionServiceImpl implements ReliefDistributionService {

    private final ReliefDistributionRepository reliefDistributionRepository;
    private final InventoryRepository inventoryRepository;
    private final MissionRepository missionRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ReliefDistributionResponse createDistribution(ReliefDistributionRequest request) {
        Mission mission = missionRepository.findById(request.getMissionId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhiệm vụ với id: " + request.getMissionId()));

        Inventory inventory = inventoryRepository.findById(request.getInventoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tồn kho với id: " + request.getInventoryId()));

        if (inventory.getQuantity() < request.getQuantity()) {
            throw new InsufficientInventoryException("Không đủ tồn kho trong kho để phân phối");
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw new BadRequestException("Người dùng chưa được xác thực");
        }

        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với email: " + email));

        // Cập nhật tồn kho
        inventory.setQuantity(inventory.getQuantity() - request.getQuantity());
        inventory.setLastUpdate(LocalDateTime.now());
        inventoryRepository.save(inventory);

        // Ghi log phân phối
        ReliefDistribution distribution = new ReliefDistribution();
        distribution.setMission(mission);
        distribution.setInventory(inventory);
        distribution.setQuantityDistributed(request.getQuantity());
        distribution.setHouseholdIdentifier(request.getHouseholdIdentifier());
        distribution.setIsConfirmed(request.getIsConfirmed() != null ? request.getIsConfirmed() : false);
        distribution.setRecordedBy(user);
        distribution.setDistributedAt(LocalDateTime.now());

        ReliefDistribution saved = reliefDistributionRepository.save(distribution);
        return mapToResponse(saved);
    }

    private ReliefDistributionResponse mapToResponse(ReliefDistribution distribution) {
        Inventory inventory = distribution.getInventory();
        String itemName = inventory.getItem().getName();
        String itemType = inventory.getItem().getItemType().name();
        User recordedBy = distribution.getRecordedBy();

        return new ReliefDistributionResponse(
                distribution.getId(),
                distribution.getMission().getId(),
                inventory.getId(),
                itemName,
                itemType,
                distribution.getQuantityDistributed(),
                distribution.getHouseholdIdentifier(),
                distribution.getIsConfirmed() != null ? distribution.getIsConfirmed() : false,
                recordedBy != null ? recordedBy.getId() : null,
                recordedBy != null ? recordedBy.getFullName() : null,
                distribution.getDistributedAt()
        );
    }
}

