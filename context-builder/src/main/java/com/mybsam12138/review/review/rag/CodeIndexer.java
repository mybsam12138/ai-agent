package com.mybsam12138.review.review.rag;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Index the entire Java codebase into method-level chunks,
 * storing them in the in-memory {@link CodeVectorStore}.
 */
@Service
@RequiredArgsConstructor
public class CodeIndexer {

    private final CodeChunker codeChunker;
    private final CodeVectorStore codeVectorStore;


    /**
     * Walk the repository directory and index all .java files as method-level chunks.
     */
    public void indexRepository(File repoRoot) throws IOException {
        Path root = repoRoot.toPath();
        if (!Files.exists(root)) {
            return;
        }

        Files.walk(root)
                .filter(p -> Files.isRegularFile(p) && p.toString().endsWith(".java"))
                .forEach(p -> {
                    try {
                        var chunks = codeChunker.chunk(p.toFile());
                        codeVectorStore.saveChunks(chunks);
                    } catch (Exception e) {
                        // Swallow individual file failures to keep indexing robust.
                    }
                });
    }
}


