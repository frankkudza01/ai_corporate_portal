package com.example.kb.entity;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "kb_chunks", indexes = {
        @Index(name = "idx_kb_chunks_doc", columnList = "document_id")
})
public class ChunkEntity {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "document_id", nullable = false)
    private UUID documentId;

    @Column(name = "vector_id", nullable = false, length = 64)
    private String vectorId;

    @Column(name = "page_number", nullable = false)
    private int pageNumber;

    @Column(name = "chunk_index", nullable = false)
    private int chunkIndex;

    @Lob
    @Column(name = "content", nullable = false)
    private String content;

    public ChunkEntity() {}

    public ChunkEntity(UUID id, UUID documentId, String vectorId, int pageNumber, int chunkIndex, String content) {
        this.id = id;
        this.documentId = documentId;
        this.vectorId = vectorId;
        this.pageNumber = pageNumber;
        this.chunkIndex = chunkIndex;
        this.content = content;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getDocumentId() { return documentId; }
    public void setDocumentId(UUID documentId) { this.documentId = documentId; }
    public String getVectorId() { return vectorId; }
    public void setVectorId(String vectorId) { this.vectorId = vectorId; }
    public int getPageNumber() { return pageNumber; }
    public void setPageNumber(int pageNumber) { this.pageNumber = pageNumber; }
    public int getChunkIndex() { return chunkIndex; }
    public void setChunkIndex(int chunkIndex) { this.chunkIndex = chunkIndex; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
