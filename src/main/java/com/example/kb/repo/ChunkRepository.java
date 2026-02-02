package com.example.kb.repo;

import com.example.kb.entity.ChunkEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ChunkRepository extends JpaRepository<ChunkEntity, UUID> {
    List<ChunkEntity> findByDocumentId(UUID documentId);
    void deleteByDocumentId(UUID documentId);
}
