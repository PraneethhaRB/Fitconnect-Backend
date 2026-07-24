package com.fitconnect.fitconnect_backend.controller;

import com.fitconnect.fitconnect_backend.dto.request.GoalUpdateRequest;
import com.fitconnect.fitconnect_backend.dto.response.ApiResponse;
import com.fitconnect.fitconnect_backend.dto.response.DashboardResponse;
import com.fitconnect.fitconnect_backend.dto.response.UserProfileResponse;
import com.fitconnect.fitconnect_backend.security.SecurityUtils;
import com.fitconnect.fitconnect_backend.service.DashboardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    // @GetMapping
    // public ResponseEntity<ApiResponse<DashboardResponse>> getDashboard(
    //         @RequestParam(required = false) Double lat,
    //         @RequestParam(required = false) Double lng) {
    //     String email = SecurityUtils.getCurrentUserEmail();
    //     DashboardResponse response = dashboardService.getDashboard(email, lat, lng);
    //     return ResponseEntity.ok(ApiResponse.success(response, "Dashboard loaded"));
    // }

    @PutMapping("/goal")
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateGoal(@Valid @RequestBody GoalUpdateRequest request) {
        String email = SecurityUtils.getCurrentUserEmail();
        UserProfileResponse response = dashboardService.updateGoal(email, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Goal updated"));
    }
    @GetMapping
public ResponseEntity<ApiResponse<DashboardResponse>> getDashboard(
        @RequestParam(required = false) Double lat,
        @RequestParam(required = false) Double lng) {
    String email = SecurityUtils.getCurrentUserEmail();
    DashboardResponse response = dashboardService.getDashboard(email, lat, lng);
    return ResponseEntity.ok(ApiResponse.success(response, "Dashboard loaded"));
}
}