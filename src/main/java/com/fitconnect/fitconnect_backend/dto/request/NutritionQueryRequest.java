package com.fitconnect.fitconnect_backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class NutritionQueryRequest {
    @NotBlank
    private String query;
}