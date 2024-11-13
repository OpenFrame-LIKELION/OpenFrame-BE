package com.openframe.openframe.api.chat.service;

import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class ChatService {

    @Value("${openai.model}")
    private String model;
    @Value("${openai.api.url}")
    private String apiUrl;
    @Value("${openai.api.key}")
    private String apiKey;

    public String getChatGPTResponse(String prompt, String type) throws IOException {
        OkHttpClient client = new OkHttpClient();

        String modifiedPrompt = switch (type.toLowerCase()) {
            case "긍정" -> "다음 주제에 대해 긍정적으로 한국어로 답변해 주세요: " + prompt;
            case "부정" -> "다음 주제에 대해 부정적으로 한국어로 답변해 주세요: " + prompt;
            case "중립" -> "다음 주제에 대해 중립적으로 한국어로 답변해 주세요: " + prompt;
            default ->
                    throw new IllegalArgumentException("유효하지 않은 타입입니다. 'positive', 'negative', 'neutral' 중 하나여야 합니다.");
        };

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("model", model);
        jsonObject.put("messages", new JSONArray()
                .put(new JSONObject()
                        .put("role", "user")
                        .put("content", modifiedPrompt)
                )
        );

        RequestBody body = RequestBody.create(
                jsonObject.toString(),
                MediaType.parse("application/json")
        );

        Request request = new Request.Builder()
                .url(apiUrl)
                .post(body)
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json")
                .build();
        
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                JSONObject responseBody = new JSONObject(response.body().string());
                return responseBody
                        .getJSONArray("choices")
                        .getJSONObject(0)
                        .getJSONObject("message")
                        .getString("content");
            } else {
                throw new IOException("Unexpected response code: " + response);
            }
        }
    }

}

