package com.fitconnect.fitconnect_backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class LeaderboardEntryResponse {
    private Long userId;
    private String name;
    private String avatarColor;
    private Integer checkInCount;
    private Integer currentStreak;
    private Integer goalProgress;
    private Integer rank;
}
