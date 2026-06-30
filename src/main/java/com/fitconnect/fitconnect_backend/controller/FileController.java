package com.fitconnect.fitconnect_backend.controller;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
public class FileController {

    private static final String UPLOAD_DIR = "./uploads";

    @GetMapping("/files/chat/{communityId}/{filename}")
    public ResponseEntity<Resource> getChatImage(@PathVariable String communityId, @PathVariable String filename) {
        try {
            Path baseDir = Paths.get(UPLOAD_DIR, "chat", communityId).normalize().toAbsolutePath();
            Path filePath = baseDir.resolve(filename).normalize();

            if (!filePath.startsWith(baseDir)) {
                return ResponseEntity.badRequest().build();
            }

            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.notFound().build();
            }

            String contentType = filename.endsWith(".png") ? "image/png"
                    : filename.endsWith(".webp") ? "image/webp"
                    : "image/jpeg";

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CACHE_CONTROL, "public, max-age=3600")
                    .body(resource);

        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}