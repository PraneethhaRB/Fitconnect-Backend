package com.fitconnect.fitconnect_backend.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserProfileResponse {
    private Long id;
    private String name;
    private String email;
    private String avatarColor;
    private String goalText;
    private Integer goalProgress;
    private Integer checkInCount;
    private LocalDateTime lastCheckInAt;
}