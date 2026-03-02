package com.floodrescue.backend.rescue.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MissionReportRequest {

    @NotNull(message = "Số người được cứu không được để trống")
    @Min(value = 0, message = "Số người được cứu phải lớn hơn hoặc bằng 0")
    private Integer peopleRescued;

    @NotBlank(message = "Tóm tắt sự cố không được để trống")
    private String summary;

    private String obstacles;
}

