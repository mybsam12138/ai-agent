package com.mybsam12138.review.review.rag;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.body.MethodDeclaration;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Service
public class CodeChunker {

    public List<String> chunk(File file) throws Exception {

        var cu = StaticJavaParser.parse(file);

        List<String> chunks = new ArrayList<>();

        for (MethodDeclaration method : cu.findAll(MethodDeclaration.class)) {
            chunks.add(method.toString());
        }

        return chunks;
    }
}