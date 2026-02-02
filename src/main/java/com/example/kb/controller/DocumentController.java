package com.example.kb.controller;

import com.example.kb.dto.DocumentDto;
import com.example.kb.entity.DocumentEntity;
import com.example.kb.exception.NotFoundException;
import com.example.kb.repo.DocumentRepository;
import com.example.kb.service.DocumentIngestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/documents")
@Tag(name = "Documents")
public class DocumentController {

    private final DocumentIngestionService ingestionService;
    private final DocumentRepository documentRepository;

    public DocumentController(DocumentIngestionService ingestionService, DocumentRepository documentRepository) {
        this.ingestionService = ingestionService;
        this.documentRepository = documentRepository;
    }

    @Operation(summary = "Upload a PDF policy/procedure")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public DocumentDto upload(@RequestPart("file") MultipartFile file, Principal principal) throws Exception {
        String uploadedBy = principal == null ? "unknown" : principal.getName();
        DocumentEntity doc = ingestionService.ingest(file, uploadedBy);
        return toDto(doc);
    }

    @Operation(summary = "List uploaded documents")
    @GetMapping
    public List<DocumentDto> list() {
        return documentRepository.findAll().stream().map(this::toDto).toList();
    }

    @Operation(summary = "Get one document metadata")
    @GetMapping("/{id}")
    public DocumentDto get(@PathVariable UUID id) {
        DocumentEntity doc = documentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Document not found: " + id));
        return toDto(doc);
    }

    @Operation(summary = "Delete a document and all indexed chunks")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        if (!documentRepository.existsById(id)) {
            throw new NotFoundException("Document not found: " + id);
        }
        ingestionService.deleteDocument(id);
    }

    private DocumentDto toDto(DocumentEntity d) {
        return new DocumentDto(d.getId(), d.getOriginalFilename(), d.getContentType(), d.getSizeBytes(), d.getUploadedBy(), d.getCreatedAt());
    }
}
