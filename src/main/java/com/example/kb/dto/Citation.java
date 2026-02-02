package com.example.kb.dto;

import java.util.UUID;

public record Citation(
        String chunkId,
        UUID documentId,
        int pageNumber,
        int chunkIndex,
        String snippet
) { }
