package com.openframe.openframe.api.chat.service;

import com.openframe.openframe.api.chat.dto.IndexRequest;
import com.openframe.openframe.api.chat.dto.MemoRequest;
import com.openframe.openframe.domain.entity.Chat;
import com.openframe.openframe.domain.entity.Index;
import com.openframe.openframe.domain.entity.Memo;
import com.openframe.openframe.domain.repository.ChatRepository;
import com.openframe.openframe.domain.repository.IndexRepository;
import com.openframe.openframe.domain.repository.MemoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ChatService {

    @Value("${openai.model}")
    private String model;
    @Value("${openai.api.url}")
    private String apiUrl;
    @Value("${openai.api.key}")
    private String apiKey;

    private final ChatRepository chatRepository;
    private final MemoRepository memoRepository;
    private final IndexRepository indexRepository;

    public List<Map<String, Object>> getChatGPTResponsesAndSave(String prompt) throws IOException {
        OkHttpClient client = new OkHttpClient();

        // Create prompts with corresponding types
        List<Map<String, String>> promptTypes = List.of(
                Map.of("prompt", "다음 주제에 대해 긍정적으로 한국어로 답변해 주세요: " + prompt, "type", "긍정"),
                Map.of("prompt", "다음 주제에 대해 긍정적으로 한국어로 답변해 주세요: " + prompt, "type", "긍정"),
                Map.of("prompt", "다음 주제에 대해 중립적으로 한국어로 답변해 주세요: " + prompt, "type", "중립"),
                Map.of("prompt", "다음 주제에 대해 부정적으로 한국어로 답변해 주세요: " + prompt, "type", "부정"),
                Map.of("prompt", "다음 주제에 대해 부정적으로 한국어로 답변해 주세요: " + prompt, "type", "부정")
        );

        List<Map<String, Object>> responseList = new ArrayList<>();

        for (Map<String, String> promptType : promptTypes) {
            String modifiedPrompt = promptType.get("prompt");
            String type = promptType.get("type");

            // Build the request payload
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
                    String content = responseBody
                            .getJSONArray("choices")
                            .getJSONObject(0)
                            .getJSONObject("message")
                            .getString("content");

                    // Save the response with type to the database
                    Chat chat = new Chat(prompt, content, type);
                    Long id = chatRepository.save(chat).getId();

                    // Add the saved response and type to the result list
                    Map<String, Object> responseMap = new HashMap<>();
                    responseMap.put("id", id);
                    responseMap.put("response", content);
                    responseMap.put("type", type);

                    responseList.add(responseMap);
                } else {
                    throw new IOException("Unexpected response code: " + response);
                }
            }
        }

        return responseList;
    }

    public Long addMemo(Long chatId, MemoRequest request) {
        Chat chat = chatRepository.findById(chatId).orElseThrow(() -> new EntityNotFoundException("Chat not found"));
        Memo memo = new Memo();
        memo.setContent(request.getContent());
        memo.setType(request.getType());
        memo.setChat(chat);
        return memoRepository.save(memo).getId();
    }

    public Long addIndex(Long chatId, IndexRequest request) {
        Chat chat = chatRepository.findById(chatId).orElseThrow(() -> new EntityNotFoundException("Chat not found"));
        Index index = new Index();
        index.setSentence(request.getSentence());
        index.setChat(chat);
        return indexRepository.save(index).getId();
    }

    public List<MemoRequest> getAllMemos(Long chatId) {
        // chatId에 해당하는 memos를 직접 조회
        List<Memo> memos = memoRepository.findAllByChatId(chatId);

        if (memos.isEmpty()) {
            throw new EntityNotFoundException("Memos not found for Chat with ID: " + chatId);
        }

        return memos.stream().map(memo -> {
            MemoRequest memoRequest = new MemoRequest();
            memoRequest.setContent(memo.getContent());
            memoRequest.setType(memo.getType());
            return memoRequest;
        }).toList();
    }

    public Long updateMemo(Long chatId, Long memoId, MemoRequest request) {
        Memo memo = memoRepository.findById(memoId)
                .filter(m -> m.getChat().getId().equals(chatId))
                .orElseThrow(() -> new EntityNotFoundException("Memo not found for chatId: " + chatId));
        memo.setContent(request.getContent());
        memo.setType(request.getType());
        return memoRepository.save(memo).getId();
    }

    public void deleteMemo(Long chatId, Long memoId) {
        Memo memo = memoRepository.findById(memoId)
                .filter(m -> m.getChat().getId().equals(chatId))
                .orElseThrow(() -> new EntityNotFoundException("Memo not found for chatId: " + chatId));
        memoRepository.delete(memo);
    }

    public List<IndexRequest> getAllIndexes(Long chatId) {
        // chatId에 해당하는 indices를 직접 조회
        List<Index> indices = indexRepository.findAllByChatId(chatId);

        if (indices.isEmpty()) {
            throw new EntityNotFoundException("Indices not found for Chat with ID: " + chatId);
        }

        return indices.stream().map(index -> {
            IndexRequest indexRequest = new IndexRequest();
            indexRequest.setSentence(index.getSentence());
            return indexRequest;
        }).toList();
    }

    public Long updateIndex(Long chatId, Long indexId, IndexRequest request) {
        Index index = indexRepository.findById(indexId)
                .filter(i -> i.getChat().getId().equals(chatId))
                .orElseThrow(() -> new EntityNotFoundException("Index not found for chatId: " + chatId));
        index.setSentence(request.getSentence());
        return indexRepository.save(index).getId();
    }

    public void deleteIndex(Long chatId, Long indexId) {
        Index index = indexRepository.findById(indexId)
                .filter(i -> i.getChat().getId().equals(chatId))
                .orElseThrow(() -> new EntityNotFoundException("Index not found for chatId: " + chatId));
        indexRepository.delete(index);
    }
}

