package com.fitconnect.fitconnect_backend.controller;

import com.fitconnect.fitconnect_backend.dto.request.GoalUpdateRequest;
import com.fitconnect.fitconnect_backend.dto.response.ApiResponse;
import com.fitconnect.fitconnect_backend.dto.response.DashboardResponse;
import com.fitconnect.fitconnect_backend.dto.response.UserProfileResponse;
import com.fitconnect.fitconnect_backend.entity.User;
import com.fitconnect.fitconnect_backend.exception.ResourceNotFoundException;
import com.fitconnect.fitconnect_backend.repository.UserRepository;
import com.fitconnect.fitconnect_backend.security.SecurityUtils;
import com.fitconnect.fitconnect_backend.service.DashboardService;
import com.fitconnect.fitconnect_backend.service.FitnessQAService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;
    private final UserRepository userRepository;
    private final FitnessQAService fitnessQAService;
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
@PostMapping("/ask")
public ResponseEntity<ApiResponse<String>> askFitnessQuestion(
        @RequestBody Map<String, String> body) {
    String email = SecurityUtils.getCurrentUserEmail();
    User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

    String question = body.get("question");
    String answer = fitnessQAService.answer(
        question,
        user.getGoalText(),
        user.getGoalCategory()  // now passed through to RAG service
    );
    return ResponseEntity.ok(ApiResponse.success(answer, "Answer generated"));
}
}
