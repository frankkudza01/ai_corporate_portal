package com.example.kb.util;

import java.util.ArrayList;
import java.util.List;

public class TextChunker {

    private final int maxChars;
    private final int overlapChars;

    public TextChunker(int maxChars, int overlapChars) {
        this.maxChars = Math.max(200, maxChars);
        this.overlapChars = Math.max(0, overlapChars);
    }

    public List<String> chunk(String text) {
        if (text == null) return List.of();
        text = text.trim();
        if (text.isEmpty()) return List.of();

        String[] paragraphs = text.split("\n\s*\n");
        List<String> chunks = new ArrayList<>();
        StringBuilder current = new StringBuilder();

        for (String para : paragraphs) {
            para = para.trim();
            if (para.isEmpty()) continue;

            if (current.length() + para.length() + 2 <= maxChars) {
                if (!current.isEmpty()) current.append("\n\n");
                current.append(para);
            } else {
                if (!current.isEmpty()) {
                    chunks.add(current.toString());
                    current = new StringBuilder(overlapTail(current.toString()));
                }
                if (para.length() > maxChars) {
                    int idx = 0;
                    while (idx < para.length()) {
                        int end = Math.min(para.length(), idx + maxChars);
                        chunks.add(para.substring(idx, end));
                        idx = end - overlapChars;
                        if (idx < 0) idx = 0;
                        if (idx >= para.length()) break;
                    }
                    current = new StringBuilder();
                } else {
                    if (!current.isEmpty()) current.append("\n\n");
                    current.append(para);
                }
            }
        }
        if (!current.isEmpty()) chunks.add(current.toString());
        return chunks;
    }

    private String overlapTail(String s) {
        if (overlapChars <= 0) return "";
        if (s.length() <= overlapChars) return s;
        return s.substring(s.length() - overlapChars);
    }
}
