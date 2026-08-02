package com.fitconnect.fitconnect_backend.service;

import com.fitconnect.fitconnect_backend.entity.GoalCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class FitnessQAService {

    @Value("${rag.service.url:http://localhost:8001}")
    private String ragServiceUrl;

    private final RestTemplate restTemplate = new RestTemplate();

   public String answer(String question, String userGoal, GoalCategory goalCategory) {
    String goalCategoryStr = goalCategory != null
            ? goalCategory.name()
            : "GENERAL_FITNESS";

    try {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = Map.of(
            "question", question != null ? question : "",
            "user_goal", userGoal != null ? userGoal : "general fitness",
            "goal_category", goalCategoryStr,
            "top_k", 3
        );

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        Map response = restTemplate.postForObject(
            ragServiceUrl + "/ask",
            entity,
            Map.class
        );
        return (String) response.get("answer");

    } catch (Exception e) {
        return "Our fitness knowledge service is temporarily unavailable. "
               + "Please try again in a moment.";
    }
}
}
