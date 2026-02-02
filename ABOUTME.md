# AI-Native Corporate Knowledge Base (Backend)
**Spring Boot 3.4 + Spring AI + PostgreSQL (pgvector)**  
A production-style backend that implements the **Retrieval-Augmented Generation (RAG)** pattern: employees upload PDFs (policies/SOPs/contracts) and then ask questions to get **instant answers with citations**.

---

## What this project demonstrates
 **Spring AI integration** (LLMs in enterprise flows)  
 **Vector database retrieval** using **PostgreSQL + pgvector**  
 **RAG pipeline**: ingest → chunk → embed → store → retrieve → answer  
 **Cited answers** (chunk IDs + page numbers + snippets)  
 **Provider flexibility**: OpenAI OR local models via Ollama  
 **Integration tests** with **Testcontainers** (real pgvector Postgres)  
 **Swagger/OpenAPI** documentation

-------------------------------------------------------------------------

## Core use case
**Internal portal workflow**
1. HR uploads `Remote_Work_Policy.pdf`
2. Employee asks: *“What’s our remote work policy?”*
3. System retrieves the most relevant document chunks and answers:
   - concise summary
   - includes citations like `[chunk-…]`
   - returns citation metadata (docId, page number, snippet)

-------------------------------------------------------------------------------------------------

## Architecture (high level)

### Ingestion (Upload)
`PDF upload → store file → extract per page → chunk → embed → upsert vectors → store metadata`

**Key components**
- **StorageService**: saves PDF to local disk (easy to swap for S3/MinIO)
- **PdfTextExtractor**: extracts text per page (PDFBox)
- **TextChunker**: chunks text with overlap
- **DocumentIngestionService**: orchestrates ingestion and indexing
- **VectorStore (pgvector)**: stores embeddings + metadata (documentId, page, chunkIndex, filename)

### Retrieval & Answering (Chat)
`question → similarity search → top K chunks → LLM prompt → answer + citations`

**Key components**
- **RagChatService**: runs similarity search and assembles citations
- **AiClient abstraction**
  - **SpringAiClient**: real LLM call (OpenAI/Ollama)
  - **MockAiClient**: deterministic offline mode for tests/dev

----------------------------------------------------------------------------------------------------

## Tech stack
- **Java 21**
- **Spring Boot 3.4.x**
- **Spring AI** (LLM + embeddings + VectorStore)
- **PostgreSQL + pgvector**
- **Flyway** (enables pgvector extension)
- **PDFBox** (PDF parsing)
- **springdoc-openapi** (Swagger UI)
- **Testcontainers** (integration testing)

