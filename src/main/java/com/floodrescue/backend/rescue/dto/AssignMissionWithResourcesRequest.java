package com.floodrescue.backend.rescue.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssignMissionWithResourcesRequest {

    /**
     * Thông tin phân công đội cứu hộ cho nhiệm vụ.
     * Bắt buộc phải có.
     */
    @Valid
    @NotNull(message = "Thông tin đội cứu hộ là bắt buộc")
    private AssignMissionRequest team;

    /**
     * Thông tin phương tiện được gán cho nhiệm vụ.
     * Có thể bỏ trống nếu chưa cần gán phương tiện.
     */
    @Valid
    private AssignVehicleRequest vehicle;

    /**
     * Danh sách vật tư/vật phẩm hỗ trợ được xuất kho cho nhiệm vụ.
     * Có thể bỏ trống hoặc rỗng nếu chưa cần xuất vật tư.
     */
    @Valid
    private List<AssignSuppliesRequest> supplies;
}

