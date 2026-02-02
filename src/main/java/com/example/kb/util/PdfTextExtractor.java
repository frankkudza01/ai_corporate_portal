package com.example.kb.util;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class PdfTextExtractor {

    public record PageText(int pageNumber, String text) { }

    public List<PageText> extractPages(InputStream pdfStream) throws Exception {
        try (PDDocument doc = PDDocument.load(pdfStream)) {
            PDFTextStripper stripper = new PDFTextStripper();
            int pages = doc.getNumberOfPages();
            List<PageText> out = new ArrayList<>(pages);

            for (int i = 1; i <= pages; i++) {
                stripper.setStartPage(i);
                stripper.setEndPage(i);
                String text = stripper.getText(doc);
                if (text == null) text = "";
                text = text.replace("\u0000", "").trim();
                out.add(new PageText(i, text));
            }
            return out;
        }
    }
}
