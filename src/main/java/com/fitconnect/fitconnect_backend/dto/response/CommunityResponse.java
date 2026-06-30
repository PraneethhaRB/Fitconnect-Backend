package com.fitconnect.fitconnect_backend.dto.response;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CommunityResponse {
    private Long id;
    private String name;
    private String description;
    private String goalFocus;
    private String coverColor;
    private Long memberCount;
    private String membershipStatus; // "NOT_JOINED" / "PENDING" / "APPROVED" — for browse list
    private boolean admin;
}