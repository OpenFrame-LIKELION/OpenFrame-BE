package com.openframe.openframe.api.chat.service;

import com.openframe.openframe.api.chat.dto.ChatRequest;
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
import java.util.List;

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

    public Long createChat(ChatRequest request) {
        Chat chat = new Chat();
        chat.setQuestion(request.getQuestion());
        return chatRepository.save(chat).getId();
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
        Chat chat = chatRepository.findById(chatId).orElseThrow(() -> new EntityNotFoundException("Chat not found"));
        return chat.getMemos().stream().map(memo -> {
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

    public List<IndexRequest> getAllIndices(Long chatId) {
        Chat chat = chatRepository.findById(chatId).orElseThrow(() -> new EntityNotFoundException("Chat not found"));
        return chat.getIndices().stream().map(index -> {
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

