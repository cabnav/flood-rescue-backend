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
public class CreateTeamRequest {
    @NotBlank(message = "Tên đội không được để trống")
    private String name;

    @NotNull(message = "Số lượng thành viên không được để trống")
    @Min(value = 0, message = "Số lượng thành viên không được nhỏ hơn 0")
    private Integer quantity;
}
