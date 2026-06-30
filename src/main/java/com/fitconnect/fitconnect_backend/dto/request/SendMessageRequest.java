package com.fitconnect.fitconnect_backend.dto.request;

import lombok.Data;

@Data
public class SendMessageRequest {
    private String text;
    private String imageCaption;
    // image file itself comes as a separate multipart part, not in this DTO
}