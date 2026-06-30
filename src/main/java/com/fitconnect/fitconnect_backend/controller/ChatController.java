package com.fitconnect.fitconnect_backend.controller;

import com.fitconnect.fitconnect_backend.dto.response.ApiResponse;
import com.fitconnect.fitconnect_backend.dto.response.ChatMessageResponse;
import com.fitconnect.fitconnect_backend.security.SecurityUtils;
import com.fitconnect.fitconnect_backend.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/communities/{communityId}/messages")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ChatMessageResponse>>> getHistory(
            @PathVariable Long communityId,
            @RequestParam(required = false) Long beforeId) {

        String email = SecurityUtils.getCurrentUserEmail();
        List<ChatMessageResponse> history = chatService.getHistory(email, communityId, beforeId);
        return ResponseEntity.ok(ApiResponse.success(history, "Messages fetched"));
    }

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<ApiResponse<ChatMessageResponse>> sendMessage(
            @PathVariable Long communityId,
            @RequestParam(required = false) String text,
            @RequestParam(required = false) String imageCaption,
            @RequestParam(required = false) MultipartFile image) {

        String email = SecurityUtils.getCurrentUserEmail();
        ChatMessageResponse response = chatService.sendMessage(email, communityId, text, imageCaption, image);
        return ResponseEntity.ok(ApiResponse.success(response, "Message sent"));
    }
}