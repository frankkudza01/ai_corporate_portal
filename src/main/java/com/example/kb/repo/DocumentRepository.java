package com.example.kb.repo;

import com.example.kb.entity.DocumentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DocumentRepository extends JpaRepository<DocumentEntity, UUID> { }
