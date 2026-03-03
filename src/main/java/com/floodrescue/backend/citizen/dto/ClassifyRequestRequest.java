package com.floodrescue.backend.citizen.dto;

import com.floodrescue.backend.citizen.model.Request;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClassifyRequestRequest {

    @NotNull(message = "Độ ưu tiên không được để trống")
    private Request.Priority priority;

    @NotNull(message = "Loại yêu cầu không được để trống")
    private Request.RequestType requestType;
}

