package com.example.datn.controller.webController;

import com.example.datn.dto.request.ChatAiMessage;
import com.example.datn.dto.request.ChatAiRequest;
import com.example.datn.dto.response.ChatAiResponse;
import com.example.datn.service.ChatAiDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat-ai")
public class ChatAiController {

    private final RestTemplate restTemplate;
    private final String geminiApiBaseUrl;
    private final ChatAiDataService chatAiDataService;

    @Value("${gemini.api.key:}")
    private String geminiApiKey;

    @PostMapping("/ask")
    public ResponseEntity<ChatAiResponse> ask(@RequestBody ChatAiRequest payload) {
        if (geminiApiKey == null || geminiApiKey.isBlank()) {
            return ResponseEntity.status(500).body(new ChatAiResponse(false, "Server chưa cấu hình gemini.api.key"));
        }

        try {
            String url = String.format("%s/models/gemini-1.5-flash:generateContent?key=%s", geminiApiBaseUrl, geminiApiKey);

            // Tạo prompt đầy đủ với ngữ cảnh sản phẩm
            String contextualPrompt = chatAiDataService.createContextualPrompt(payload.getPrompt());

            List<Map<String, Object>> contents = new ArrayList<>();
            if (payload.getHistory() != null) {
                for (ChatAiMessage m : payload.getHistory()) {
                    String role = ("user".equalsIgnoreCase(m.getRole())) ? "user" : "model";
                    contents.add(Map.of("role", role, "parts", List.of(Map.of("text", Optional.ofNullable(m.getText()).orElse("")))));
                }
            }
            // Sử dụng contextual prompt thay vì prompt gốc
            contents.add(Map.of("role", "user", "parts", List.of(Map.of("text", contextualPrompt))));

            Map<String, Object> body = Map.of("contents", contents);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

            Map<?, ?> resp = restTemplate.postForObject(url, entity, Map.class);
            String text = "";
            try {
                Map<?, ?> candidate0 = ((List<Map<?, ?>>) resp.get("candidates")).get(0);
                Map<?, ?> content = (Map<?, ?>) candidate0.get("content");
                List<Map<?, ?>> parts = (List<Map<?, ?>>) content.get("parts");
                text = Objects.toString(parts.get(0).get("text"), "");
            } catch (Exception ignore) { }

            return ResponseEntity.ok(new ChatAiResponse(true, text));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(new ChatAiResponse(false, ex.getMessage()));
        }
    }
}


