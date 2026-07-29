package com.fitconnect.fitconnect_backend.service;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class FitnessKnowledgeBase {

    // In a real RAG system this would be a vector database (Pinecone, Chroma, pgvector)
    // For this implementation we use keyword-matched chunks — same concept, simpler retrieval
    private static final Map<String, String> KNOWLEDGE_CHUNKS = new HashMap<>() {{
        put("calories weight loss", """
            For weight loss, a caloric deficit of 300-500 calories per day is recommended.
            This creates sustainable loss of 0.3-0.5kg per week. Protein intake should be
            1.6-2.2g per kg of bodyweight to preserve muscle mass. Avoid deficits above
            1000 calories as they cause muscle loss and metabolic adaptation.
            """);
        put("muscle gain protein", """
            For muscle gain (hypertrophy), consume 1.6-2.2g protein per kg bodyweight daily.
            Caloric surplus of 200-300 calories supports muscle growth without excess fat.
            Distribute protein across 4-6 meals. Complete proteins (meat, eggs, dairy, soy)
            provide all essential amino acids needed for muscle synthesis.
            """);
        put("workout frequency training", """
            Research supports training each muscle group 2x per week for optimal hypertrophy.
            A push/pull/legs split or upper/lower split achieves this efficiently.
            Beginners benefit from 3 full-body sessions per week. Advanced lifters may train
            4-6 days. Rest periods of 48-72 hours between same muscle groups prevents overtraining.
            """);
        put("cardio endurance running", """
            For cardiovascular fitness, the American Heart Association recommends 150 minutes
            of moderate-intensity or 75 minutes of vigorous cardio per week. Zone 2 training
            (60-70% max heart rate) builds aerobic base efficiently. HIIT (High-Intensity
            Interval Training) improves VO2 max and burns more calories in less time.
            """);
        put("sleep recovery rest", """
            Sleep is the primary recovery mechanism. 7-9 hours per night is optimal for
            athletic performance. During deep sleep, growth hormone is released which repairs
            muscle tissue. Sleep deprivation reduces testosterone, increases cortisol, impairs
            protein synthesis, and decreases reaction time and decision-making ability.
            """);
        put("nutrition diet food", """
            A balanced diet for fitness includes: complex carbohydrates (oats, rice, sweet potato)
            for energy, lean protein (chicken, fish, eggs) for muscle repair, healthy fats
            (avocado, nuts, olive oil) for hormone production, and vegetables for micronutrients.
            Timing: consume carbs around workouts, protein throughout the day, reduce simple
            carbs in the evening.
            """);
    }};

    public String retrieve(String query) {
        String lowerQuery = query.toLowerCase();
        StringBuilder retrieved = new StringBuilder();

        KNOWLEDGE_CHUNKS.forEach((keywords, content) -> {
            String[] keywordArray = keywords.split(" ");
            for (String keyword : keywordArray) {
                if (lowerQuery.contains(keyword)) {
                    retrieved.append(content).append("\n");
                    break;
                }
            }
        });

        return retrieved.length() > 0 ? retrieved.toString()
                : "General fitness principle: consistency beats intensity. "
                + "Sustainable habits over time produce better results than extreme approaches.";
    }
}