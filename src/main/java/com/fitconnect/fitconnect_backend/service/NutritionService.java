package com.fitconnect.fitconnect_backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;

@Service
public class NutritionService {

    @Value("${usda.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public NutritionResponse analyze(String query) {
        try {
            String url = UriComponentsBuilder
                .fromUriString("https://api.nal.usda.gov/fdc/v1/foods/search")
                .queryParam("query", query)
                .queryParam("pageSize", 1)
                .queryParam("api_key", apiKey)
                .toUriString();

            Map response = restTemplate.getForObject(url, Map.class);
            List<Map> foods = (List<Map>) response.get("foods");

            if (foods == null || foods.isEmpty()) return null;

            Map food = foods.get(0);
            String name = (String) food.getOrDefault("description", query);
            List<Map> nutrients = (List<Map>) food.get("foodNutrients");

            if (nutrients == null) return null;

            int calories = 0, protein = 0, carbs = 0, fat = 0;

            for (Map nutrient : nutrients) {
                String nutrientName = (String) nutrient.getOrDefault("nutrientName", "");
                double value = nutrient.get("value") != null
                    ? ((Number) nutrient.get("value")).doubleValue() : 0;

                if (nutrientName.contains("Energy"))        calories = (int) value;
                else if (nutrientName.contains("Protein"))  protein  = (int) value;
                else if (nutrientName.contains("Carbohydrate")) carbs = (int) value;
                else if (nutrientName.equals("Total lipid (fat)")) fat = (int) value;
            }

            return new NutritionResponse(name, calories, protein, carbs, fat);

        } catch (Exception e) {
            return null;
        }
    }

    public record NutritionResponse(
        String foodName,
        int calories,
        int proteinG,
        int carbsG,
        int fatG
    ) {}
}