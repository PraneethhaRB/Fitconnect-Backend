package com.fitconnect.fitconnect_backend.dto.response;

public record WorkoutSuggestionResponse(
    String title,
    String description,
    String type,
    Integer temperatureCelsius
) {}