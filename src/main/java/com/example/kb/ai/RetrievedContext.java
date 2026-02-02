package com.example.kb.ai;

import java.util.Map;

public record RetrievedContext(
        String id,
        String text,
        Map<String, Object> metadata,
        double score
) { }
