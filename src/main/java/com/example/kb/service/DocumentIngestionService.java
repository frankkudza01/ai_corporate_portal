package com.example.kb.service;

import com.example.kb.config.KbProperties;
import com.example.kb.entity.ChunkEntity;
import com.example.kb.entity.DocumentEntity;
import com.example.kb.exception.BadRequestException;
import com.example.kb.repo.ChunkRepository;
import com.example.kb.repo.DocumentRepository;
import com.example.kb.util.PdfTextExtractor;
import com.example.kb.util.TextChunker;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.OffsetDateTime;
import java.util.*;

@Service
public class DocumentIngestionService {

    private final StorageService storageService;
    private final DocumentRepository documentRepository;
    private final ChunkRepository chunkRepository;
    private final VectorStore vectorStore;
    private final KbProperties props;

    public DocumentIngestionService(StorageService storageService,
                                    DocumentRepository documentRepository,
                                    ChunkRepository chunkRepository,
                                    VectorStore vectorStore,
                                    KbProperties props) {
        this.storageService = storageService;
        this.documentRepository = documentRepository;
        this.chunkRepository = chunkRepository;
        this.vectorStore = vectorStore;
        this.props = props;
    }

    public DocumentEntity ingest(MultipartFile file, String uploadedBy) throws Exception {
        if (file == null || file.isEmpty()) throw new BadRequestException("File is required.");
        String ct = (file.getContentType() == null ? "" : file.getContentType().toLowerCase(Locale.ROOT));
        if (!ct.contains("pdf")) throw new BadRequestException("Only PDF uploads are supported.");

        UUID docId = UUID.randomUUID();
        StorageService.StoredFile stored = storageService.store(file, docId);

        DocumentEntity doc = new DocumentEntity(
                docId,
                file.getOriginalFilename() == null ? "document.pdf" : file.getOriginalFilename(),
                file.getContentType(),
                file.getSize(),
                stored.path(),
                uploadedBy,
                OffsetDateTime.now()
        );
        documentRepository.save(doc);

        PdfTextExtractor extractor = new PdfTextExtractor();
        TextChunker chunker = new TextChunker(props.chunking().maxChars(), props.chunking().overlapChars());
        List<PdfTextExtractor.PageText> pages = extractor.extractPages(file.getInputStream());

        List<Document> vectorDocs = new ArrayList<>();
        List<ChunkEntity> chunks = new ArrayList<>();

        int globalChunkIndex = 0;
        for (PdfTextExtractor.PageText page : pages) {
            List<String> pageChunks = chunker.chunk(page.text());
            int localIdx = 0;
            for (String chunkText : pageChunks) {
                String vectorId = "chunk-" + UUID.randomUUID();

                Map<String, Object> md = new HashMap<>();
                md.put("documentId", docId.toString());
                md.put("pageNumber", page.pageNumber());
                md.put("chunkIndex", globalChunkIndex);
                md.put("localChunkIndex", localIdx);
                md.put("filename", doc.getOriginalFilename());

                vectorDocs.add(new Document(vectorId, chunkText, md));
                chunks.add(new ChunkEntity(UUID.randomUUID(), docId, vectorId, page.pageNumber(), globalChunkIndex, chunkText));

                globalChunkIndex++;
                localIdx++;
            }
        }

        chunkRepository.saveAll(chunks);
        vectorStore.add(vectorDocs);

        return doc;
    }

    public void deleteDocument(UUID docId) {
        List<ChunkEntity> chunks = chunkRepository.findByDocumentId(docId);
        List<String> vectorIds = chunks.stream().map(ChunkEntity::getVectorId).toList();
        if (!vectorIds.isEmpty()) vectorStore.delete(vectorIds);

        chunkRepository.deleteByDocumentId(docId);
        documentRepository.deleteById(docId);
    }
}
