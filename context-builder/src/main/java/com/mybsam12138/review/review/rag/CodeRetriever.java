package com.mybsam12138.review.review.rag;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CodeRetriever {

    private final CodeVectorStore codeVectorStore;

    /**
     * Search for the most relevant code chunks using the provided symbol queries.
     */
    public List<String> searchBySymbols(List<String> symbols, int topK) {
        if (symbols == null || symbols.isEmpty()) {
            return List.of();
        }
        String query = String.join(" ", symbols);
        return codeVectorStore.similaritySearch(query, topK);
    }
}