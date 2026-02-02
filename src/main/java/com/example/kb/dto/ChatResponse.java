package com.example.kb.dto;

import java.util.List;

public record ChatResponse(
        String answer,
        List<Citation> citations
) { }
