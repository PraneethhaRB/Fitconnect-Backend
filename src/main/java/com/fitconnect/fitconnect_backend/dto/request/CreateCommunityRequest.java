package com.fitconnect.fitconnect_backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateCommunityRequest {
    @NotBlank(message = "Name is required")
    private String name;

    private String description;

    @NotBlank(message = "Goal focus is required")
    private String goalFocus; // LOSE_WEIGHT, BUILD_MUSCLE, HEALTHY_LIFESTYLE

    private String coverColor;
}