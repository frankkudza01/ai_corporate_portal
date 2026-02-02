package com.example.kb.service;

import com.example.kb.ai.AiClient;
import com.example.kb.ai.RetrievedContext;
import com.example.kb.config.KbProperties;
import com.example.kb.dto.Citation;
import com.example.kb.dto.ChatResponse;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RagChatService {

    private final VectorStore vectorStore;
    private final AiClient aiClient;
    private final KbProperties props;

    public RagChatService(VectorStore vectorStore, AiClient aiClient, KbProperties props) {
        this.vectorStore = vectorStore;
        this.aiClient = aiClient;
        this.props = props;
    }

    public ChatResponse ask(String question, List<UUID> documentIds) {
        List<Document> matches = vectorStore.similaritySearch(question);

        if (documentIds != null && !documentIds.isEmpty()) {
            Set<String> allowed = documentIds.stream().map(UUID::toString).collect(Collectors.toSet());
            matches = matches.stream()
                    .filter(d -> allowed.contains(String.valueOf(d.getMetadata().get("documentId"))))
                    .toList();
        }

        int k = Math.max(1, props.retrieval().topK());
        if (matches.size() > k) matches = matches.subList(0, k);

        List<RetrievedContext> contexts = new ArrayList<>();
        List<Citation> citations = new ArrayList<>();

        for (Document d : matches) {
            String id = d.getId();
            String text = d.getContent() == null ? "" : d.getContent();
            Map<String, Object> md = d.getMetadata() == null ? Map.of() : d.getMetadata();

            double score = 0.0;
            Object sc = md.get("distance");
            if (sc instanceof Number n) score = n.doubleValue();

            contexts.add(new RetrievedContext(id, safeTrim(text, 1800), md, score));

            UUID docId = tryUuid(String.valueOf(md.get("documentId")));
            int page = tryInt(md.get("pageNumber"));
            int chunkIndex = tryInt(md.get("chunkIndex"));
            citations.add(new Citation(id, docId, page, chunkIndex, safeTrim(text, 260)));
        }

        String answer = aiClient.answer(question, contexts);
        return new ChatResponse(answer, citations);
    }

    private String safeTrim(String s, int max) {
        if (s == null) return "";
        s = s.trim();
        if (s.length() <= max) return s;
        return s.substring(0, max) + "...";
    }

    private int tryInt(Object o) {
        if (o == null) return 0;
        if (o instanceof Number n) return n.intValue();
        try { return Integer.parseInt(String.valueOf(o)); } catch (Exception e) { return 0; }
    }

    private UUID tryUuid(String s) {
        try { return UUID.fromString(s); } catch (Exception e) { return null; }
    }
}
