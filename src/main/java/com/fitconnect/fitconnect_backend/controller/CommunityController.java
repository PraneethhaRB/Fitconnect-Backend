package com.fitconnect.fitconnect_backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fitconnect.fitconnect_backend.dto.request.CreateCommunityRequest;
import com.fitconnect.fitconnect_backend.dto.response.ApiResponse;
import com.fitconnect.fitconnect_backend.dto.response.CommunityResponse;
import com.fitconnect.fitconnect_backend.dto.response.LeaderboardEntryResponse;
import com.fitconnect.fitconnect_backend.dto.response.MembershipResponse;
import com.fitconnect.fitconnect_backend.security.SecurityUtils;
import com.fitconnect.fitconnect_backend.service.CommunityService;

import jakarta.validation.Valid;

import java.util.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/communities")
public class CommunityController {
@Autowired
CommunityService  communityService;
@PostMapping("/{communityId}/join")
public ResponseEntity<ApiResponse<Object>> requestToJoin(@PathVariable Long communityId) {
    //TODO: process POST request
    String email = SecurityUtils.getCurrentUserEmail();
    communityService.requestToJoin(email, communityId);
    return ResponseEntity.ok(ApiResponse.success(null, "join request submitted"));
}
 @GetMapping("/{communityId}/pending")
    public ResponseEntity<ApiResponse<List<MembershipResponse>>> getPendingRequests(@PathVariable Long communityId) {
        String email = SecurityUtils.getCurrentUserEmail();
        List<MembershipResponse> pending = communityService.getPendingRequests(email, communityId);
        return ResponseEntity.ok(ApiResponse.success(pending, "Pending requests fetched"));
    }

    @PostMapping("/{communityId}/approve/{membershipId}")
    public ResponseEntity<ApiResponse<Object>> approve(@PathVariable Long communityId, @PathVariable Long membershipId) {
        String email = SecurityUtils.getCurrentUserEmail();
        communityService.approveRequest(email, communityId, membershipId);
        return ResponseEntity.ok(ApiResponse.success(null, "Member approved"));
    }

    @PostMapping("/{communityId}/reject/{membershipId}")
    public ResponseEntity<ApiResponse<Object>> reject(@PathVariable Long communityId, @PathVariable Long membershipId) {
        String email = SecurityUtils.getCurrentUserEmail();
        communityService.rejectRequest(email, communityId, membershipId);
        return ResponseEntity.ok(ApiResponse.success(null, "Member rejected"));
    }
    @PostMapping
public ResponseEntity<ApiResponse<CommunityResponse>> create(@Valid @RequestBody CreateCommunityRequest request) {
    String email = SecurityUtils.getCurrentUserEmail();
    CommunityResponse response = communityService.createCommunity(email, request);
    return ResponseEntity.ok(ApiResponse.success(response, "Community created"));
}
@GetMapping("/{communityId}/leaderboard")
public ResponseEntity<ApiResponse<List<LeaderboardEntryResponse>>> getLeaderboard(
        @PathVariable Long communityId) {
    List<LeaderboardEntryResponse> leaderboard =
            communityService.getLeaderboard(communityId);
    return ResponseEntity.ok(ApiResponse.success(leaderboard, "Leaderboard fetched"));
}

}
