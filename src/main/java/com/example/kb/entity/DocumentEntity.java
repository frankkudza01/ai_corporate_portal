package com.example.kb.entity;

import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "kb_documents")
public class DocumentEntity {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "original_filename", nullable = false, length = 512)
    private String originalFilename;

    @Column(name = "content_type", length = 128)
    private String contentType;

    @Column(name = "size_bytes")
    private long sizeBytes;

    @Column(name = "storage_path", nullable = false, length = 1024)
    private String storagePath;

    @Column(name = "uploaded_by", length = 128)
    private String uploadedBy;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    public DocumentEntity() {}

    public DocumentEntity(UUID id, String originalFilename, String contentType, long sizeBytes, String storagePath, String uploadedBy, OffsetDateTime createdAt) {
        this.id = id;
        this.originalFilename = originalFilename;
        this.contentType = contentType;
        this.sizeBytes = sizeBytes;
        this.storagePath = storagePath;
        this.uploadedBy = uploadedBy;
        this.createdAt = createdAt;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getOriginalFilename() { return originalFilename; }
    public void setOriginalFilename(String originalFilename) { this.originalFilename = originalFilename; }
    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }
    public long getSizeBytes() { return sizeBytes; }
    public void setSizeBytes(long sizeBytes) { this.sizeBytes = sizeBytes; }
    public String getStoragePath() { return storagePath; }
    public void setStoragePath(String storagePath) { this.storagePath = storagePath; }
    public String getUploadedBy() { return uploadedBy; }
    public void setUploadedBy(String uploadedBy) { this.uploadedBy = uploadedBy; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
}
