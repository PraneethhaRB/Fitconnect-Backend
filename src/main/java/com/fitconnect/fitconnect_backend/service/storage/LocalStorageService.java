package com.fitconnect.fitconnect_backend.service.storage;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class LocalStorageService implements StorageService {

    @Value("${app.upload.dir}")
    private String uploadDir;

    @Value("${app.base-url}")
    private String baseUrl;

    private static final List<String> ALLOWED_TYPES =
            List.of("image/jpeg", "image/png", "image/webp");

    @Override
    public String store(MultipartFile file, Long communityId) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType)) {
            throw new IllegalArgumentException("Only JPEG, PNG, or WEBP images are allowed");
        }

        String extension = switch (contentType) {
            case "image/jpeg" -> ".jpg";
            case "image/png" -> ".png";
            case "image/webp" -> ".webp";
            default -> "";
        };

        String filename = UUID.randomUUID() + extension;

        try {
            Path communityDir = Paths.get(uploadDir, "chat", communityId.toString()).normalize().toAbsolutePath();
            Files.createDirectories(communityDir);

            Path targetPath = communityDir.resolve(filename).normalize();

            // Path traversal guard: resolved path must still be inside communityDir
            if (!targetPath.startsWith(communityDir)) {
                throw new SecurityException("Invalid file path");
            }

            file.transferTo(targetPath);

        } catch (IOException e) {
            throw new RuntimeException("Failed to store file", e);
        }

        return baseUrl + "/files/chat/" + communityId + "/" + filename;
    }
}