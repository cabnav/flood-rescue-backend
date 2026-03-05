package com.floodrescue.backend.citizen.service;

import com.floodrescue.backend.citizen.model.Request;
import com.floodrescue.backend.citizen.model.RequestMedia;
import com.floodrescue.backend.citizen.repository.RequestMediaRepository;
import com.floodrescue.backend.citizen.repository.RequestRepository;
import com.floodrescue.backend.citizen.service.RequestMediaService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RequestMediaServiceImpl implements RequestMediaService {

    private final RequestRepository requestRepository;
    private final RequestMediaRepository requestMediaRepository;
    private final S3Client s3Client;

    @Value("${r2.bucket-name}")
    private String bucketName;

    @Value("${r2.public-base-url}")
    private String publicBaseUrl;

    @Override
    public String uploadMedia(Integer requestId, MultipartFile file) {

        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Yêu cầu không tìm thấy"));

        long maxSize = 30 * 1024 * 1024;
        if (file.getSize() > maxSize) {
            throw new RuntimeException("Tệp quá lớn");
        }

        String contentType = file.getContentType();
        if (contentType == null) {
            throw new RuntimeException("Loại nội dung tệp là null");
        }

        RequestMedia.MediaType mediaType;

        if (contentType.startsWith("image/")) {
            mediaType = RequestMedia.MediaType.IMAGE;
        } else if (contentType.startsWith("video/")) {
            mediaType = RequestMedia.MediaType.VIDEO;
        } else {
            throw new RuntimeException("Loại tệp không hợp lệ: " + contentType);
        }

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
            String key = "requests/" + requestId + "/" + storedFileName;

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(
                    putObjectRequest,
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize())
            );
        } catch (IOException e) {
            throw new RuntimeException("Không thể tải tệp lên đám mây", e);
        }

        String fileUrl = publicBaseUrl + "/requests/" + requestId + "/" + storedFileName;

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
