package com.example.kb.ai;

import java.util.List;

public interface AiClient {
    String answer(String question, List<RetrievedContext> contexts);
}
