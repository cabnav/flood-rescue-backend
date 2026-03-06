package com.floodrescue.backend.citizen.service;

import com.floodrescue.backend.citizen.dto.RequestMediaResponse;
import org.springframework.web.multipart.MultipartFile;

public interface RequestMediaService {

    RequestMediaResponse uploadMedia(Integer requestId, MultipartFile file);

}