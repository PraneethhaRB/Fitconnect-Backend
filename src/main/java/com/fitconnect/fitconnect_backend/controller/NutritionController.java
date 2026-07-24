package com.fitconnect.fitconnect_backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fitconnect.fitconnect_backend.dto.request.NutritionQueryRequest;
import com.fitconnect.fitconnect_backend.dto.response.ApiResponse;
import com.fitconnect.fitconnect_backend.service.NutritionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/nutrition")
@RequiredArgsConstructor

public class NutritionController {
    private final NutritionService nutritionService;

    @PostMapping("/analyze")
    public ResponseEntity<ApiResponse<NutritionService.NutritionResponse>> analyze(
            @Valid @RequestBody NutritionQueryRequest request) {
        NutritionService.NutritionResponse result =
                nutritionService.analyze(request.getQuery());
        if (result == null) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Could not analyze that food"));
        }
        return ResponseEntity.ok(ApiResponse.success(result, "Nutrition analyzed"));
    }
}
