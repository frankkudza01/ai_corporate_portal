package com.example.kb.controller;

import com.example.kb.dto.ChatRequest;
import com.example.kb.dto.ChatResponse;
import com.example.kb.service.RagChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
@Tag(name = "Chat")
public class ChatController {

    private final RagChatService ragChatService;

    public ChatController(RagChatService ragChatService) {
        this.ragChatService = ragChatService;
    }

    @Operation(summary = "Ask a question against indexed documents (RAG)")
    @PostMapping
    public ChatResponse ask(@Valid @RequestBody ChatRequest request) {
        return ragChatService.ask(request.question(), request.documentIds());
    }
}
