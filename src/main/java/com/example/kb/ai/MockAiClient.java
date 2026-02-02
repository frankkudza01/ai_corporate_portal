package com.example.kb.ai;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Profile({"mock","test"})
public class MockAiClient implements AiClient {

    @Override
    public String answer(String question, List<RetrievedContext> contexts) {
        if (contexts == null || contexts.isEmpty()) {
            return "I don't know based on the currently indexed documents.";
        }
        RetrievedContext top = contexts.get(0);
        String snippet = top.text();
        if (snippet.length() > 240) snippet = snippet.substring(0, 240) + "...";
        return "Based on the policy documents, here is what I found: " + snippet + " [" + top.id() + "]";
    }
}
