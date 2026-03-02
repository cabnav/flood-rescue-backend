package com.floodrescue.backend.citizen.service;

import com.floodrescue.backend.citizen.model.Request;
import com.floodrescue.backend.citizen.model.RequestMedia;
import com.floodrescue.backend.citizen.repository.RequestMediaRepository;
import com.floodrescue.backend.citizen.repository.RequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class RequestMediaService {

    private final RequestRepository requestRepository;
    private final RequestMediaRepository requestMediaRepository;

    public String uploadMedia(Integer requestId, MultipartFile file) {

        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        // Validate file size (30MB max)
        long maxSize = 30 * 1024 * 1024;
        if (file.getSize() > maxSize) {
            throw new RuntimeException("File too large");
        }

        // Determine media type
        String contentType = file.getContentType();

        if (contentType == null) {
            throw new RuntimeException("File content type is null");
        }

        RequestMedia.MediaType mediaType;

        if (contentType.startsWith("image/")) {
            mediaType = RequestMedia.MediaType.IMAGE;
        }
        else if (contentType.startsWith("video/")) {
            mediaType = RequestMedia.MediaType.VIDEO;
        }
        else {
            throw new RuntimeException("Invalid file type: " + contentType);
        }

        // TODO: upload file to cloud or local storage
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isBlank()) {
            originalFilename = "file";
        }

        String extension = "";
        int dotIndex = originalFilename.lastIndexOf('.');
        if (dotIndex != -1 && dotIndex < originalFilename.length() - 1) {
            extension = originalFilename.substring(dotIndex);
        }

        String storedFileName = "request-" + requestId + "-" + UUID.randomUUID() + extension;

        try {
            Path requestDir = Paths.get(uploadDir, "requests", String.valueOf(requestId))
                    .toAbsolutePath()
                    .normalize();
            Files.createDirectories(requestDir);

            Path targetLocation = requestDir.resolve(storedFileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Could not store file", e);
        }

        // Public URL mapping (served via WebMvc resource handler)
        String fileUrl = "/uploads/requests/" + requestId + "/" + storedFileName;

        RequestMedia media = RequestMedia.builder()
                .request(request)
                .mediaUrl(fileUrl)
                .mediaType(mediaType)
                .fileSize(file.getSize())
                .mimeType(file.getContentType())
                .build();

        requestMediaRepository.save(media);

        return fileUrl;
    }
}