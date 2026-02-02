package com.example.kb.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record DocumentDto(
        UUID id,
        String originalFilename,
        String contentType,
        long sizeBytes,
        String uploadedBy,
        OffsetDateTime createdAt
) { }
