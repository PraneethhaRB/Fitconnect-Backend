package com.fitconnect.fitconnect_backend.service;

import com.fitconnect.fitconnect_backend.entity.GoalCategory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fitconnect.fitconnect_backend.dto.response.WorkoutSuggestionResponse;
import java.util.Map;

@Service
public class WeatherService {

    private final RestTemplate restTemplate = new RestTemplate();

    public WorkoutSuggestionResponse getSuggestion(Double lat, Double lng, GoalCategory goalCategory) {
        if (lat == null || lng == null) {
            return defaultSuggestion(goalCategory);
        }

        String url = String.format(
            "https://api.open-meteo.com/v1/forecast" +
            "?latitude=%s&longitude=%s" +
            "&current=temperature_2m,precipitation,windspeed_10m",
            lat, lng
        );

        try {
            Map response = restTemplate.getForObject(url, Map.class);
            Map current = (Map) response.get("current");

            double temp = ((Number) current.get("temperature_2m")).doubleValue();
            double rain = ((Number) current.get("precipitation")).doubleValue();
            double wind = ((Number) current.get("windspeed_10m")).doubleValue();

            return buildSuggestion(temp, rain, wind, goalCategory);

        } catch (Exception e) {
            return defaultSuggestion(goalCategory);
        }
    }

    private WorkoutSuggestionResponse buildSuggestion(
            double temp, double rain, double wind, GoalCategory goal) {

        boolean isRainy = rain > 1.0;
        boolean isWindy = wind > 25.0;
        boolean isCold = temp < 10.0;
        boolean isHot = temp > 35.0;
        boolean isGood = !isRainy && !isWindy && !isCold && !isHot;

        if (goal == GoalCategory.WEIGHT_LOSS) {
            if (isGood)
                return new WorkoutSuggestionResponse("Perfect for a run 🏃",
                    "Great conditions outside — go for a 30-min run or brisk walk.",
                    "OUTDOOR", (int) Math.round(temp));
            if (isRainy)
                return new WorkoutSuggestionResponse("Home HIIT day 🏠",
                    "It's raining — perfect time for a 20-min home HIIT session.",
                    "INDOOR", (int) Math.round(temp));
            if (isHot)
                return new WorkoutSuggestionResponse("Early morning or gym 🌅",
                    "Too hot for outdoor cardio — hit the gym or go early morning.",
                    "INDOOR", (int) Math.round(temp));
            // cold or windy — falls through to here
            return new WorkoutSuggestionResponse("Layer up and move 🧥",
                    "Cold or windy outside — dress in layers for a walk or head indoors.",
                    "INDOOR", (int) Math.round(temp));
        }

        if (goal == GoalCategory.MUSCLE_GAIN) {
            return new WorkoutSuggestionResponse("Gym session 💪",
                "Strength training isn't weather-dependent — hit the weights today.",
                "INDOOR",(int) Math.round(temp));
        }

        if (goal == GoalCategory.ENDURANCE) {
            if (isGood)
                return new WorkoutSuggestionResponse("Long run conditions 🏃",
                    "Ideal conditions for a long run or cycling session.",
                    "OUTDOOR",(int) Math.round(temp));
            if (isRainy || isWindy)
                return new WorkoutSuggestionResponse("Indoor cardio 🚴",
                    "Tough conditions outside — treadmill or stationary bike today.",
                    "INDOOR", (int)Math.round(temp));
        }

        return defaultSuggestion(goal);
    }

    private WorkoutSuggestionResponse defaultSuggestion(GoalCategory goal) {
        return new WorkoutSuggestionResponse(
            "Stay active today 🏋️",
            "Any movement counts — even a 20-minute walk toward your goal.",
            "ANY",
            null
        );
    }

    // public record WorkoutSuggestion(
    //     String title,
    //     String description,
    //     String type,
    //     Integer temperatureCelsius
    // ) {}
}