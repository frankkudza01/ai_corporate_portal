package com.example.kb.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.List;
import java.util.UUID;

public record ChatRequest(
        @NotBlank String question,
        List<UUID> documentIds
) { }
