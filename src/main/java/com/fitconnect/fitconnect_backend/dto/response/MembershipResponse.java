package com.fitconnect.fitconnect_backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class MembershipResponse {
    private Long membershipId;
    private Long userId;
    private String userName;
    private String userAvatarColor;
    private LocalDateTime requestedAt;
}