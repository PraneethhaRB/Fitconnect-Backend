package com.fitconnect.fitconnect_backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FitnessQAService {

    @Value("${groq.api.key}")
private String groqKey;

    private final FitnessKnowledgeBase knowledgeBase;
    private final RestTemplate restTemplate = new RestTemplate();

    public String answer(String question, String userGoal) {
        // Step 1: RETRIEVE relevant context from knowledge base
        String retrievedContext = knowledgeBase.retrieve(question);

        // Step 2: AUGMENT the prompt with retrieved context
        String prompt = String.format("""
            You are a knowledgeable fitness advisor. Answer the user's question
            using ONLY the provided context below. If the context doesn't cover
            the question, say so and give a brief general answer.
            
            User's current goal: %s
            
            Retrieved fitness knowledge:
            ---
            %s
            ---
            
            User's question: %s
            
            Provide a specific, practical answer in 2-3 sentences.
            """, userGoal, retrievedContext, question);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(groqKey);

        Map<String, Object> body = Map.of(
            "model", "llama-3.3-70b-versatile",
            "max_tokens", 200,
            "messages", List.of(
                Map.of("role", "user", "content", prompt)
            )
        );
        try {
            Map response = restTemplate.postForObject(
                "https://api.groq.com/openai/v1/chat/completions",
                new HttpEntity<>(body, headers),
                Map.class
            );
            List<Map> choices = (List<Map>) response.get("choices");
            Map message = (Map) choices.get(0).get("message");
            return (String) message.get("content");
            
        } catch (Exception e) {
            return "I couldn't retrieve an answer right now. Please try again.";
        }
    }
}