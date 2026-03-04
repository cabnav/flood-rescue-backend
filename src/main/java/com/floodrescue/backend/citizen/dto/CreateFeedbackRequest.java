package com.floodrescue.backend.citizen.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateFeedbackRequest {

    @NotNull(message = "Bạn cần xác nhận trạng thái an toàn")
    private Boolean isSafe;

    @NotBlank(message = "Vui lòng nhập mô tả tình trạng của bạn / phản hồi hỗ trợ")
    @Size(max = 2000, message = "Comment must be at most 2000 characters")
    private String comment;
}

