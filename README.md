# AI-Native Corporate Knowledge Base (Backend)

Spring Boot + Spring AI + PostgreSQL(pgvector) backend implementing Retrieval-Augmented Generation (RAG):
- Upload PDFs/policies
- Index chunks into pgvector
- Ask questions and get answers with citations

## Quick start (Docker)
```bash
docker compose up -d postgres
# optional local LLM
docker compose up -d ollama
```

Run API (mock mode, no external AI):
```bash
mvn -q -DskipTests spring-boot:run -Dspring-boot.run.profiles=mock
```

Run API with OpenAI:
```bash
export OPENAI_API_KEY="..."
mvn -q -DskipTests spring-boot:run -Dspring-boot.run.profiles=openai
```

Run API with Ollama:
```bash
mvn -q -DskipTests spring-boot:run -Dspring-boot.run.profiles=ollama
```

Swagger:
- http://localhost:8080/swagger-ui/index.html

Basic Auth (default):
- kb_admin / kb_admin

## Note on vector dimensions
Different embedding models output different vector sizes.
Set env `KB_VECTOR_DIMENSIONS` to match your embedding model output.
