package com.openframe.openframe.api.chat.presentation;

import com.openframe.openframe.api.chat.dto.IndexRequest;
import com.openframe.openframe.api.chat.dto.MemoRequest;
import com.openframe.openframe.api.chat.service.ChatService;
import com.openframe.openframe.common.ApplicationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/api/chat")
public class ChatController {

    private final ChatService chatService;

    @GetMapping()
    public ApplicationResponse<List<Map<String, Object>>> getChatGptOpenApi(
            @RequestParam String keyword
    ) throws IOException {

        final List<Map<String, Object>> chatGPTResponse = chatService.getChatGPTResponsesAndSave(keyword);
        return ApplicationResponse.ok(chatGPTResponse);
    }

    @PostMapping("/{chatId}/memo")
    public ApplicationResponse<Long> addMemo(@PathVariable Long chatId, @RequestBody MemoRequest request) {
        Long memoId = chatService.addMemo(chatId, request);
        return ApplicationResponse.ok(memoId);
    }

    @PostMapping("/{chatId}/index")
    public ApplicationResponse<Long> addIndex(@PathVariable Long chatId, @RequestBody IndexRequest request) {
        Long indexId = chatService.addIndex(chatId, request);
        return ApplicationResponse.ok(indexId);
    }

    @GetMapping("/{chatId}/memo")
    public ApplicationResponse<List<MemoRequest>> getAllMemos(@PathVariable Long chatId) {
        List<MemoRequest> memos = chatService.getAllMemos(chatId);
        return ApplicationResponse.ok(memos);
    }

    @PutMapping("/{chatId}/memo/{memoId}")
    public ApplicationResponse<Long> updateMemo(@PathVariable Long chatId, @PathVariable Long memoId, @RequestBody MemoRequest request) {
        Long updatedMemoId = chatService.updateMemo(chatId, memoId, request);
        return ApplicationResponse.ok(updatedMemoId);
    }

    @DeleteMapping("/{chatId}/memo/{memoId}")
    public ApplicationResponse<Void> deleteMemo(@PathVariable Long chatId, @PathVariable Long memoId) {
        chatService.deleteMemo(chatId, memoId);
        return ApplicationResponse.ok(null);
    }

    @GetMapping("/{chatId}/index")
    public ApplicationResponse<List<IndexRequest>> getAllIndices(@PathVariable Long chatId) {
        List<IndexRequest> indices = chatService.getAllIndexes(chatId);
        return ApplicationResponse.ok(indices);
    }

    @PutMapping("/{chatId}/index/{indexId}")
    public ApplicationResponse<Long> updateIndex(@PathVariable Long chatId, @PathVariable Long indexId, @RequestBody IndexRequest request) {
        Long updatedIndexId = chatService.updateIndex(chatId, indexId, request);
        return ApplicationResponse.ok(updatedIndexId);
    }

    @DeleteMapping("/{chatId}/index/{indexId}")
    public ApplicationResponse<Void> deleteIndex(@PathVariable Long chatId, @PathVariable Long indexId) {
        chatService.deleteIndex(chatId, indexId);
        return ApplicationResponse.ok(null);
    }
}
