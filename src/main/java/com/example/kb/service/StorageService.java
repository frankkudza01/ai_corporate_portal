package com.example.kb.service;

import com.example.kb.config.KbProperties;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Service
public class StorageService {

    private final Path uploadDir;

    public StorageService(KbProperties props) {
        this.uploadDir = Path.of(props.storage().uploadDir()).toAbsolutePath().normalize();
    }

    public StoredFile store(MultipartFile file, UUID docId) throws Exception {
        Files.createDirectories(uploadDir);

        String originalName = StringUtils.cleanPath(file.getOriginalFilename() == null ? "document.pdf" : file.getOriginalFilename());
        String safeName = originalName.replaceAll("[^a-zA-Z0-9._-]+", "_");

        Path target = uploadDir.resolve(docId + "_" + safeName).normalize();
        try (InputStream in = file.getInputStream()) {
            Files.copy(in, target);
        }
        return new StoredFile(target.toString());
    }

    public record StoredFile(String path) { }
}
