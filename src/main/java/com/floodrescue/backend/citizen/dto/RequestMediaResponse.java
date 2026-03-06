package com.floodrescue.backend.citizen.dto;

import lombok.*;
import java.time.LocalDateTime;
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RequestMediaResponse {
    private Integer id;
    private String mediaUrl;
    private String mediaType;
    private Long fileSize;
    private String mimeType;
    private LocalDateTime createdAt;
}
