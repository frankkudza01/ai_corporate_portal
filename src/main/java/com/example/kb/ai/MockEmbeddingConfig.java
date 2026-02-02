package com.example.kb.ai;

import org.springframework.ai.embedding.Embedding;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Configuration
@Profile({"mock","test"})
public class MockEmbeddingConfig {

    @Bean
    public EmbeddingModel embeddingModel() {
        return new EmbeddingModel() {
            private static final int DIM = 1536;

            @Override
            public EmbeddingResponse call(EmbeddingRequest request) {
                List<Embedding> embeddings = new ArrayList<>();
                for (String input : request.getInstructions()) {
                    embeddings.add(new Embedding(hashToVector(input, DIM), 0));
                }
                return new EmbeddingResponse(embeddings);
            }

            private List<Double> hashToVector(String text, int dim) {
                byte[] bytes = text.getBytes(StandardCharsets.UTF_8);
                long h = 1469598103934665603L;
                for (byte b : bytes) {
                    h ^= (b & 0xff);
                    h *= 1099511628211L;
                }
                List<Double> v = new ArrayList<>(dim);
                long x = h;
                for (int i = 0; i < dim; i++) {
                    x ^= (x << 13);
                    x ^= (x >>> 7);
                    x ^= (x << 17);
                    double val = ((x & 0xffff) / 32767.5) - 1.0;
                    v.add(val);
                }
                return v;
            }
        };
    }
}
