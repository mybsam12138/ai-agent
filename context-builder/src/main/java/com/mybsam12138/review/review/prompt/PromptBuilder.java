package com.mybsam12138.review.review.prompt;

import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Build the final prompt string that will be pasted into ChatGPT.
 * This component does NOT call any LLM API.
 */
@Service
public class PromptBuilder {

    public String buildPrompt(String diff, List<String> relatedCode) {

        StringBuilder prompt = new StringBuilder();

        prompt.append("""
                You are reviewing a Java code change.

                Changed diff:

                """);

        prompt.append(diff == null ? "" : diff);

        prompt.append("\n\nRelevant code context:\n\n");

        if (relatedCode != null && !relatedCode.isEmpty()) {
            for (String code : relatedCode) {
                prompt.append("----- CODE CHUNK START -----\n");
                prompt.append(code);
                prompt.append("\n----- CODE CHUNK END -----\n\n");
            }
        } else {
            prompt.append("(no additional context retrieved)\n");
        }

        prompt.append("\nPlease review the change.");

        return prompt.toString();
    }
}


