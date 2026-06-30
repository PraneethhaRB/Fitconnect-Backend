package com.fitconnect.fitconnect_backend.dto.response;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class DashboardResponse {
    private UserProfileResponse user;
    private List<CommunityResponse> joinedCommunities;
    private List<CommunityResponse> recommendedCommunities;
    private List<LabOfferResponse> nearbyLabOffers;
    private List<CommunityResponse> pendingCommunities;
}