// package com.fitconnect.fitconnect_backend.service;

// import com.fitconnect.fitconnect_backend.entity.User;
// import com.fitconnect.fitconnect_backend.entity.ChatMessage;
// import com.fitconnect.fitconnect_backend.repository.ChatMessageRepository;
// import com.fitconnect.fitconnect_backend.repository.UserRepository;
// import lombok.RequiredArgsConstructor;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.data.domain.PageRequest;
// import org.springframework.http.*;
// import org.springframework.stereotype.Service;
// import org.springframework.web.client.RestTemplate;

// import java.util.List;
// import java.util.Map;

// @Service
// @RequiredArgsConstructor
// public class CoachAgentService {

//     @Value("${anthropic.api.key}")
//     private String anthropicKey;

//     private final UserRepository userRepository;
//     private final ChatMessageRepository chatMessageRepository;
//     private final ChatService chatService;
//     private final RestTemplate restTemplate = new RestTemplate();

//     public String generateCoachingInsight(Long userId, Long communityId) {
//         User user = userRepository.findById(userId)
//                 .orElseThrow(() -> new RuntimeException("User not found"));

//         // Tool 1: Read user's recent messages (last 10)
//         List<ChatMessage> recentMessages = chatMessageRepository
//                 .findByCommunityIdOrderBySentAtDesc(communityId, PageRequest.of(0, 10));

//         StringBuilder messageHistory = new StringBuilder();
//         recentMessages.forEach(m -> {
//             if (m.getSender().getId().equals(userId)) {
//                 messageHistory.append("User said: ").append(m.getText()).append("\n");
//                 if (m.getImageUrl() != null) {
//                     messageHistory.append("User posted a progress photo\n");
//                 }
//             }
//         });

//         // Tool 2: Read user's current stats
//         String userContext = String.format(
//             "User: %s\nGoal: %s\nProgress: %d%%\nCheck-ins: %d\nCurrent streak: %d days\nLongest streak: %d days\nGoal category: %s",
//             user.getName(),
//             user.getGoalText(),
//             user.getGoalProgress() != null ? user.getGoalProgress() : 0,
//             user.getCheckInCount() != null ? user.getCheckInCount() : 0,
//             user.getCurrentStreak() != null ? user.getCurrentStreak() : 0,
//             user.getLongestStreak() != null ? user.getLongestStreak() : 0,
//             user.getGoalCategory() != null ? user.getGoalCategory().name() : "GENERAL_FITNESS"
//         );

//         // Build the agent prompt
//         String systemPrompt = """
//             You are a fitness coach agent for FitConnect, a fitness community app.
//             You have access to a user's real fitness data and community activity.
//             Your job is to analyze their progress and generate a specific, personalized,
//             actionable coaching message — not generic advice.
            
//             Rules:
//             - Be specific to THEIR data, not generic
//             - If their streak is 0 and they haven't checked in recently, address that directly
//             - If they're making great progress, celebrate specifically what they've done
//             - End with one concrete action they should take today
//             - Keep it under 150 words
//             - Sound human and encouraging, not robotic
//             - Do not mention that you are an AI
//             """;

//         String userPrompt = String.format("""
//             Here is the user's data:
//             %s
            
//             Recent community activity:
//             %s
            
//             Generate a personalized coaching message for this user right now.
//             """, userContext, messageHistory.toString().isEmpty()
//                 ? "No recent messages" : messageHistory.toString());

//         // Call Anthropic API
//         HttpHeaders headers = new HttpHeaders();
//         headers.setContentType(MediaType.APPLICATION_JSON);
//         headers.set("x-api-key", anthropicKey);
//         headers.set("anthropic-version", "2023-06-01");

//         Map<String, Object> body = Map.of(
//             "model", "claude-3-haiku-20240307",
//             "max_tokens", 300,
//             "system", systemPrompt,
//             "messages", List.of(
//                 Map.of("role", "user", "content", userPrompt)
//             )
//         );

//         HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

//         try {
//             Map response = restTemplate.postForObject(
//                 "https://api.anthropic.com/v1/messages",
//                 entity,
//                 Map.class
//             );

//             List<Map> content = (List<Map>) response.get("content");
//             return (String) content.get(0).get("text");

//         } catch (Exception e) {
//             return "Keep going! Every check-in counts toward your goal.";
//         }
//     }
// }
package com.fitconnect.fitconnect_backend.service;

import com.fitconnect.fitconnect_backend.entity.User;
import com.fitconnect.fitconnect_backend.entity.ChatMessage;
import com.fitconnect.fitconnect_backend.repository.ChatMessageRepository;
import com.fitconnect.fitconnect_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CoachAgentService {

    @Value("${groq.api.key}")
    private String groqKey;

    private final UserRepository userRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    public String generateCoachingInsight(Long userId, Long communityId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<ChatMessage> recentMessages = chatMessageRepository
                .findByCommunityIdOrderBySentAtDesc(communityId, PageRequest.of(0, 10));

        StringBuilder messageHistory = new StringBuilder();
        recentMessages.forEach(m -> {
            if (m.getSender().getId().equals(userId)) {
                messageHistory.append("User said: ").append(m.getText()).append("\n");
                if (m.getImageUrl() != null) {
                    messageHistory.append("User posted a progress photo\n");
                }
            }
        });

        String userContext = String.format(
            "User: %s\nGoal: %s\nProgress: %d%%\nCheck-ins: %d\nCurrent streak: %d days\nLongest streak: %d days",
            user.getName(),
            user.getGoalText(),
            user.getGoalProgress() != null ? user.getGoalProgress() : 0,
            user.getCheckInCount() != null ? user.getCheckInCount() : 0,
            user.getCurrentStreak() != null ? user.getCurrentStreak() : 0,
            user.getLongestStreak() != null ? user.getLongestStreak() : 0
        );

        String systemPrompt = """
            You are a fitness coach agent for FitConnect, a fitness community app.
            Analyze the user's real data and generate a specific, actionable coaching
            message. Be specific to their data, not generic. End with one concrete
            action for today. Keep it under 150 words. Do not mention you are an AI.
            """;

        String userPrompt = String.format("""
            User data:
            %s

            Recent activity:
            %s

            Generate a personalized coaching message.
            """, userContext, messageHistory.length() == 0 ? "No recent messages" : messageHistory);

        // --- Groq uses OpenAI-style chat format ---
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(groqKey);   // "Authorization: Bearer <key>"

        Map<String, Object> body = Map.of(
            "model", "llama-3.3-70b-versatile",
            "max_tokens", 300,
            "messages", List.of(
                Map.of("role", "system", "content", systemPrompt),
                Map.of("role", "user", "content", userPrompt)
            )
        );

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            Map response = restTemplate.postForObject(
                "https://api.groq.com/openai/v1/chat/completions",
                entity,
                Map.class
            );

            // Groq's response shape: choices[0].message.content
            List<Map> choices = (List<Map>) response.get("choices");
            Map message = (Map) choices.get(0).get("message");
            return (String) message.get("content");

        } catch (Exception e) {
            return "Keep going! Every check-in counts toward your goal.";
        }
    }
}