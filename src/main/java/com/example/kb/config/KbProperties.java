package com.example.kb.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "kb")
public record KbProperties(
        Storage storage,
        Chunking chunking,
        Retrieval retrieval
) {
    public record Storage(String uploadDir) {}
    public record Chunking(int maxChars, int overlapChars) {}
    public record Retrieval(int topK) {}
}
