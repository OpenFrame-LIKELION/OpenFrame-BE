package com.openframe.openframe.api.chat.presentation;

import com.openframe.openframe.api.chat.service.ChatService;
import com.openframe.openframe.common.ApplicationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

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
}
