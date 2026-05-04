package com.swp391.cclearly.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.swp391.cclearly.dto.response.PrescriptionAiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.client.HttpStatusCodeException;

import java.util.Base64;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class GeminiService {

    @Value("${gemini.api.key:}")
    private String geminiApiKey;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RestTemplate restTemplate = new RestTemplate();

    public PrescriptionAiResponse extractPrescription(MultipartFile file) {
        try {
            String base64Image = Base64.getEncoder().encodeToString(file.getBytes());
            String mimeType = file.getContentType();
            if (mimeType == null) mimeType = "image/jpeg";

            // Prompt chuyên nghiệp cho Gemini 2.5
            String prompt = "You are a professional optometrist. Extract prescription data into JSON.\n" +
                    "CRITICAL: Check both eyes (OD/OS) for SPH, CYL, AXIS, and ADD.\n" +
                    "Fields: od_sph, od_cyl, od_axs, od_add, os_sph, os_cyl, os_axs, os_add, pd, note.\n" +
                    "The 'note' field must be a professional Vietnamese summary.";

            Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                    Map.of("parts", List.of(
                        Map.of("text", prompt),
                        Map.of("inline_data", Map.of("mime_type", mimeType, "data", base64Image))
                    ))
                )
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            // SỬ DỤNG MODEL GEMINI 2.5 FLASH (BẢN STABLE 2026)
            String model = "gemini-2.5-flash";
            String url = "https://generativelanguage.googleapis.com/v1/models/" + model + ":generateContent?key=" + geminiApiKey.trim();
            
            log.info("Requesting Gemini 2.5 Flash...");
            String response = restTemplate.postForObject(url, entity, String.class);
            
            JsonNode root = objectMapper.readTree(response);
            String aiText = root.path("candidates").get(0).path("content").path("parts").get(0).path("text").asText();
            
            // Xử lý JSON từ AI (loại bỏ markdown)
            String jsonStr = aiText.replaceAll("```json", "").replaceAll("```", "").trim();
            log.info("AI Success Response: {}", jsonStr);

            return objectMapper.readValue(jsonStr, PrescriptionAiResponse.class);

        } catch (Exception e) {
            String errorMsg = e.getMessage();
            if (e instanceof HttpStatusCodeException) {
                errorMsg = ((HttpStatusCodeException) e).getResponseBodyAsString();
            }
            log.error("Gemini 2.5 Error: {}", errorMsg);
            throw new RuntimeException("Lỗi AI (2.5): " + errorMsg);
        }
    }

    public PrescriptionAiResponse chatPrescription(String userMessage) {
        try {
            String prompt = "You are a professional optometrist assistant. A customer is describing their eye condition.\n" +
                    "Extract or suggest potential values. Return ONLY JSON.\n" +
                    "Fields: od_sph, od_cyl, od_axs, od_add, os_sph, os_cyl, os_axs, os_add, pd, note.\n" +
                    "Vietnamese advice in 'note'.\n" +
                    "Message: " + userMessage;

            Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                    Map.of("parts", List.of(Map.of("text", prompt)))
                )
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            String url = "https://generativelanguage.googleapis.com/v1/models/gemini-2.5-flash:generateContent?key=" + geminiApiKey.trim();
            String response = restTemplate.postForObject(url, entity, String.class);
            
            JsonNode root = objectMapper.readTree(response);
            String aiText = root.path("candidates").get(0).path("content").path("parts").get(0).path("text").asText();
            String jsonStr = aiText.replaceAll("```json", "").replaceAll("```", "").trim();

            return objectMapper.readValue(jsonStr, PrescriptionAiResponse.class);
        } catch (Exception e) {
            log.error("Gemini Chat Error: {}", e.getMessage());
            throw new RuntimeException("Lỗi Chat AI: " + e.getMessage());
        }
    }
}
