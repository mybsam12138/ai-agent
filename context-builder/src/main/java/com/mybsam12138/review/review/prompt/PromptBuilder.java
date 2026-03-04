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
                You are a senior Java engineer performing a code review.

                Review the following Java code change.

                Focus primarily on the modified lines in the diff.
                Use the provided code context only if it helps understand the change.

                Review the change for the following issues:

                - compilation problems
                - incorrect or unused imports
                - Spring Boot configuration mistakes
                - logic errors
                - potential runtime exceptions
                - maintainability issues
                - performance concerns
                - security risks
                - violations of common Java or Spring best practices

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

        prompt.append("""
                                
                Instructions:

                1. Only comment on issues introduced or affected by the change.
                2. Do not review unrelated code.
                3. Be concise and actionable.

                Output format:

                Issue:
                Severity: LOW | MEDIUM | HIGH
                Explanation:
                Suggested Fix:

                If there are no issues, respond with:
                "No problems detected."
                """);


        return prompt.toString();
    }
}


