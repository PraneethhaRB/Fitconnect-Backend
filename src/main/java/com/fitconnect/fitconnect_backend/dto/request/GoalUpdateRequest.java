package com.fitconnect.fitconnect_backend.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class GoalUpdateRequest {
    private String goalText;

    @Min(0)
    @Max(100)
    private Integer goalProgress;
}