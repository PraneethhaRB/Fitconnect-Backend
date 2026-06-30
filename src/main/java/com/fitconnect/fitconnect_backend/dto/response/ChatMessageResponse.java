package com.fitconnect.fitconnect_backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ChatMessageResponse {
    private Long id;
    private Long senderId;
    private String senderName;
    private String senderAvatarColor;
    private String text;
    private String imageUrl;
    private String imageCaption;
    private LocalDateTime sentAt;
}