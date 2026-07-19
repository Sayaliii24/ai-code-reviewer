package com.example.aicodereview.service;

import com.example.aicodereview.dto.AnalysisIssue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public String getAiReview(List<File> javaFiles, List<AnalysisIssue> staticIssues) {
        if (javaFiles.isEmpty()) return "No Java files provided for review.";

        StringBuilder prompt = new StringBuilder();
        prompt.append("You are an expert AI Code Review Assistant. I am providing you with Java source code files and a list of static analysis issues found by PMD. ");
        prompt.append("Please provide a comprehensive code review covering: Bug detection, Code smell analysis, Performance improvements, Security recommendations, and Refactoring suggestions. Format your response in clean Markdown.\n\n");
        prompt.append("IMPORTANT: At the very end of your response, you MUST provide the fully refactored and fixed version of the source code. Wrap this fixed code exactly inside `<fixed_code>` and `</fixed_code>` XML tags. Do NOT use markdown code blocks inside the xml tags. Just the raw text inside the xml tags.\n\n");

        prompt.append("--- STATIC ANALYSIS ISSUES ---\n");
        if (staticIssues.isEmpty()) {
            prompt.append("No static analysis issues found.\n");
        } else {
            for (AnalysisIssue issue : staticIssues) {
                prompt.append("- [").append(issue.getPriority()).append("] in ")
                      .append(issue.getFile()).append(" (Line ").append(issue.getBeginLine()).append("): ")
                      .append(issue.getMessage()).append("\n");
            }
        }
        prompt.append("\n--- SOURCE CODE ---\n");

        for (File file : javaFiles) {
            try {
                prompt.append("File: ").append(file.getName()).append("\n");
                prompt.append("```java\n");
                prompt.append(Files.readString(file.toPath()));
                prompt.append("\n```\n\n");
            } catch (Exception e) {
                prompt.append("Could not read file: ").append(file.getName()).append("\n\n");
            }
        }

        return callGeminiApi(prompt.toString());
    }

    private String callGeminiApi(String prompt) {
        String urlWithKey = apiUrl + "?key=" + apiKey;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Build the Gemini API Request Body
        Map<String, Object> requestBody = new HashMap<>();
        Map<String, Object> contents = new HashMap<>();
        Map<String, Object> parts = new HashMap<>();
        parts.put("text", prompt);
        contents.put("parts", List.of(parts));
        requestBody.put("contents", List.of(contents));

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        try {
            Map<String, Object> response = restTemplate.postForObject(urlWithKey, request, Map.class);
            if (response != null && response.containsKey("candidates")) {
                List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.get("candidates");
                if (!candidates.isEmpty()) {
                    Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
                    List<Map<String, Object>> responseParts = (List<Map<String, Object>>) content.get("parts");
                    return (String) responseParts.get(0).get("text");
                }
            }
            return "Failed to parse AI response.";
        } catch (Exception e) {
            return "Error calling Gemini API: " + e.getMessage();
        }
    }
}
