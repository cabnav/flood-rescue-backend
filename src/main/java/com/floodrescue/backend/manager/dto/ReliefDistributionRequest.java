package com.floodrescue.backend.manager.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReliefDistributionRequest {

    @NotNull(message = "Mã nhiệm vụ không được để trống")
    private Integer missionId;

    @NotNull(message = "Mã tồn kho không được để trống")
    private Integer inventoryId;

    @NotNull(message = "Số lượng phân phối không được để trống")
    @Min(value = 0, message = "Số lượng phải lớn hơn hoặc bằng 0")
    private Integer quantity;

    @NotBlank(message = "CCCD/Định danh hộ gia đình không được để trống")
    private String householdIdentifier;

    /** For confirmation-of-receipt flow; defaults to false when creating. */
    private Boolean isConfirmed;
}

