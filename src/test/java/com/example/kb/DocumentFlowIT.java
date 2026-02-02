package com.example.kb;

import com.example.kb.dto.ChatRequest;
import com.example.kb.dto.ChatResponse;
import com.example.kb.dto.DocumentDto;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.ByteArrayOutputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"test","mock"})
class DocumentFlowIT {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("pgvector/pgvector:pg16")
            .withDatabaseName("kb")
            .withUsername("kb")
            .withPassword("kb");

    @LocalServerPort
    int port;

    TestRestTemplate rest = new TestRestTemplate("kb_admin", "kb_admin");

    @BeforeAll
    static void start() {
        postgres.start();

        System.setProperty("KB_DB_URL", postgres.getJdbcUrl());
        System.setProperty("KB_DB_USER", postgres.getUsername());
        System.setProperty("KB_DB_PASSWORD", postgres.getPassword());

        // pgvector store properties (best-effort; names vary by Spring AI version)
        System.setProperty("KB_PGV_HOST", postgres.getHost());
        System.setProperty("KB_PGV_PORT", String.valueOf(postgres.getMappedPort(5432)));
        System.setProperty("KB_PGV_DB", "kb");
        System.setProperty("KB_PGV_USER", "kb");
        System.setProperty("KB_PGV_PASSWORD", "kb");
        System.setProperty("KB_PGV_TABLE", "kb_vectors");
        System.setProperty("KB_VECTOR_DIMENSIONS", "1536");
    }

    @AfterAll
    static void stop() {
        postgres.stop();
    }

    @Test
    void uploadThenAsk_returnsCitedAnswer() throws Exception {
        String policy =
                "Remote Work Policy:\n" +
                "Employees may work remotely up to 3 days per week with manager approval.\n" +
                "Security: Always use VPN and company-managed devices.\n";

        byte[] pdf = makePdf(policy);

        String url = "http://localhost:" + port + "/api/documents";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        var body = new org.springframework.util.LinkedMultiValueMap<String, Object>();
        body.add("file", new NamedBytesResource(pdf, "remote-work-policy.pdf"));

        ResponseEntity<DocumentDto> uploaded = rest.postForEntity(url, new HttpEntity<>(body, headers), DocumentDto.class);
        assertEquals(HttpStatus.OK, uploaded.getStatusCode());
        assertNotNull(uploaded.getBody());
        assertNotNull(uploaded.getBody().id());

        String chatUrl = "http://localhost:" + port + "/api/chat";
        ChatRequest req = new ChatRequest("What is our remote work policy?", List.of(uploaded.getBody().id()));
        ResponseEntity<ChatResponse> resp = rest.postForEntity(chatUrl, req, ChatResponse.class);

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertNotNull(resp.getBody());
        assertTrue(resp.getBody().answer().toLowerCase().contains("remote"));
        assertNotNull(resp.getBody().citations());
        assertFalse(resp.getBody().citations().isEmpty());
    }

    private byte[] makePdf(String text) throws Exception {
        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.LETTER);
            doc.addPage(page);

            try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {
                cs.beginText();
                cs.setFont(PDType1Font.HELVETICA, 12);
                cs.newLineAtOffset(50, 700);
                for (String line : text.split("\n")) {
                    cs.showText(line.replace("\t", " "));
                    cs.newLineAtOffset(0, -16);
                }
                cs.endText();
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            doc.save(out);
            return out.toByteArray();
        }
    }

    static class NamedBytesResource extends ByteArrayResource {
        private final String filename;
        NamedBytesResource(byte[] data, String filename) {
            super(data);
            this.filename = filename;
        }
        @Override
        public String getFilename() {
            return filename;
        }
    }
}
