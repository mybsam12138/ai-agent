package com.mybsam12138.review.review.rag;

import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * Simple in-memory "vector" store based on token-set Jaccard similarity.
 * No external LLM or embedding API is used.
 */
@Service
public class CodeVectorStore {

    private final List<StoredChunk> chunks = new CopyOnWriteArrayList<>();

    public void saveChunks(List<String> newChunks) {
        for (String content : newChunks) {
            if (content == null || content.isBlank()) {
                continue;
            }
            StoredChunk storedChunk = new StoredChunk(content, tokenize(content));
            chunks.add(storedChunk);
        }
    }

    /**
     * Run a simple similarity search using Jaccard similarity over token sets.
     */
    public List<String> similaritySearch(String query, int topK) {
        if (query == null || query.isBlank() || chunks.isEmpty()) {
            return List.of();
        }

        Set<String> queryTokens = tokenize(query);
        if (queryTokens.isEmpty()) {
            return List.of();
        }

        return chunks.stream()
                .map(chunk -> new ScoredChunk(chunk.content(), jaccard(queryTokens, chunk.tokens())))
                .sorted(Comparator.comparingDouble(ScoredChunk::score).reversed())
                .limit(topK)
                .map(ScoredChunk::content)
                .collect(Collectors.toList());
    }

    private Set<String> tokenize(String text) {
        String[] raw = text.toLowerCase(Locale.ROOT).split("\\W+");
        Set<String> tokens = new HashSet<>();
        for (String t : raw) {
            if (t.length() >= 2) {
                tokens.add(t);
            }
        }
        return tokens;
    }

    private double jaccard(Set<String> a, Set<String> b) {
        if (a.isEmpty() || b.isEmpty()) {
            return 0.0;
        }
        Set<String> intersection = new HashSet<>(a);
        intersection.retainAll(b);

        Set<String> union = new HashSet<>(a);
        union.addAll(b);

        return union.isEmpty() ? 0.0 : (double) intersection.size() / union.size();
    }

    private record StoredChunk(String content, Set<String> tokens) {
    }

    private record ScoredChunk(String content, double score) {
    }
}