package com.mybsam12138.review.review;

import com.mybsam12138.review.git.GitService;
import com.mybsam12138.review.review.parser.JavaSymbolExtractor;
import com.mybsam12138.review.review.prompt.PromptBuilder;
import com.mybsam12138.review.review.rag.CodeIndexer;
import com.mybsam12138.review.review.rag.CodeRetriever;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final GitService gitService;
    private final PromptBuilder promptBuilder;
    private final JavaSymbolExtractor javaSymbolExtractor;
    private final CodeIndexer codeIndexer;
    private final CodeRetriever codeRetriever;

    public String reviewCurrentBranch(String repoPath, String baseBranch) throws Exception {

        // 1. Compute Git diff between HEAD~1 and HEAD
        String diff = gitService.getUnifiedDiff(repoPath);

        if (diff == null || diff.isBlank()) {
            return "No diff detected.";
        }

        // 2. Index the whole repository as method-level chunks
        codeIndexer.indexRepository(new File(repoPath));

        // 3. Determine which Java files changed in the latest commit
        List<String> changedJavaFiles = gitService.getChangedJavaFiles(repoPath);

        // 4. Extract symbol queries (class names, method names, imports) from changed files
        Set<String> symbolQueries = new HashSet<>();
        for (String relPath : changedJavaFiles) {
            File file = new File(repoPath, relPath);
            if (file.isFile()) {
                symbolQueries.addAll(javaSymbolExtractor.extractSymbolQueries(file));
            }
        }

        List<String> symbols = new ArrayList<>(symbolQueries);

        // 5. Retrieve top-k relevant code chunks using similarity search
        List<String> relatedCode = codeRetriever.searchBySymbols(symbols, 5);

        // 6. Build final prompt string for manual copy-paste into ChatGPT
        return promptBuilder.buildPrompt(diff, relatedCode);
    }
}