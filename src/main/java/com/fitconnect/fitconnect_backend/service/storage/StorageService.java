package com.fitconnect.fitconnect_backend.service.storage;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
String store(MultipartFile file,Long communityId);
}
