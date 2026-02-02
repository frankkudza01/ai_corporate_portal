package com.example.kb.ai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Profile({"openai","ollama"})
public class SpringAiClient implements AiClient {

    private final ChatClient chatClient;

    public SpringAiClient(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    @Override
    public String answer(String question, List<RetrievedContext> contexts) {
        String contextBlock = contexts.stream()
                .map(c -> "- [" + c.id() + "] " + c.text())
                .collect(Collectors.joining("\n"));

        String system =
                "You are a corporate knowledge base assistant.\n" +
                "Answer ONLY using the provided context.\n" +
                "If the answer isn't in the context, say you don't know and suggest what document might contain it.\n" +
                "Keep answers concise, professional, and cite chunk ids in square brackets like [chunk-id].\n";

        String user = "CONTEXT:\n" + contextBlock + "\n\nQUESTION:\n" + question;

        return chatClient.prompt()
                .system(system)
                .user(user)
                .call()
                .content();
    }
}
