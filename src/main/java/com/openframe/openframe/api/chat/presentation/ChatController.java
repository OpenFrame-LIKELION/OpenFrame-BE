package com.openframe.openframe.api.chat.presentation;

import com.openframe.openframe.api.chat.dto.ChatRequest;
import com.openframe.openframe.api.chat.dto.IndexRequest;
import com.openframe.openframe.api.chat.dto.MemoRequest;
import com.openframe.openframe.api.chat.service.ChatService;
import com.openframe.openframe.common.ApplicationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/api/chat")
public class ChatController {

    private final ChatService chatService;

    @GetMapping()
    public ApplicationResponse<String> getChatGptOpenApi(
            @RequestParam String keyword,
            @RequestParam String type
    ) throws IOException {

        final String chatGPTResponse = chatService.getChatGPTResponse(keyword, type);
        return ApplicationResponse.ok(chatGPTResponse);
    }

    @PostMapping
    public ResponseEntity<Long> createChat(@RequestBody ChatRequest request) {
        Long chatId = chatService.createChat(request);
        return ResponseEntity.ok(chatId);
    }

    @PostMapping("/{chatId}/memo")
    public ResponseEntity<Long> addMemo(@PathVariable Long chatId, @RequestBody MemoRequest request) {
        Long memoId = chatService.addMemo(chatId, request);
        return ResponseEntity.ok(memoId);
    }

    @PostMapping("/{chatId}/index")
    public ResponseEntity<Long> addIndex(@PathVariable Long chatId, @RequestBody IndexRequest request) {
        Long indexId = chatService.addIndex(chatId, request);
        return ResponseEntity.ok(indexId);
    }
    @GetMapping("/{chatId}/memo")
    public ResponseEntity<List<MemoRequest>> getAllMemos(@PathVariable Long chatId) {
        List<MemoRequest> memos = chatService.getAllMemos(chatId);
        return ResponseEntity.ok(memos);
    }

    @PutMapping("/{chatId}/memo/{memoId}")
    public ResponseEntity<Long> updateMemo(@PathVariable Long chatId, @PathVariable Long memoId, @RequestBody MemoRequest request) {
        Long updatedMemoId = chatService.updateMemo(chatId, memoId, request);
        return ResponseEntity.ok(updatedMemoId);
    }

    @DeleteMapping("/{chatId}/memo/{memoId}")
    public ResponseEntity<Void> deleteMemo(@PathVariable Long chatId, @PathVariable Long memoId) {
        chatService.deleteMemo(chatId, memoId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{chatId}/index")
    public ResponseEntity<List<IndexRequest>> getAllIndices(@PathVariable Long chatId) {
        List<IndexRequest> indices = chatService.getAllIndices(chatId);
        return ResponseEntity.ok(indices);
    }

    @PutMapping("/{chatId}/index/{indexId}")
    public ResponseEntity<Long> updateIndex(@PathVariable Long chatId, @PathVariable Long indexId, @RequestBody IndexRequest request) {
        Long updatedIndexId = chatService.updateIndex(chatId, indexId, request);
        return ResponseEntity.ok(updatedIndexId);
    }

    @DeleteMapping("/{chatId}/index/{indexId}")
    public ResponseEntity<Void> deleteIndex(@PathVariable Long chatId, @PathVariable Long indexId) {
        chatService.deleteIndex(chatId, indexId);
        return ResponseEntity.noContent().build();
    }
}
