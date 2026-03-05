package com.floodrescue.backend.citizen.service;

import org.springframework.web.multipart.MultipartFile;

public interface RequestMediaService {

    String uploadMedia(Integer requestId, MultipartFile file);

}