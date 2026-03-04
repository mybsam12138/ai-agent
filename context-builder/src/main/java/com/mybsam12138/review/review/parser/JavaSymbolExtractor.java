package com.mybsam12138.review.review.parser;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
public class JavaSymbolExtractor {

    public List<MethodDeclaration> extractMethods(File file) throws Exception {

        CompilationUnit cu = StaticJavaParser.parse(file);
        return cu.findAll(MethodDeclaration.class);
    }

    /**
     * Extract symbol strings (class names, method names, imports) from a Java file.
     * These symbols are used as text queries for similarity search over the codebase.
     */
    public List<String> extractSymbolQueries(File file) throws Exception {

        CompilationUnit cu = StaticJavaParser.parse(file);

        Set<String> symbols = new LinkedHashSet<>();

        // Class and interface names
        for (ClassOrInterfaceDeclaration type : cu.findAll(ClassOrInterfaceDeclaration.class)) {
            symbols.add(type.getNameAsString());
        }

        // Method names
        for (MethodDeclaration method : cu.findAll(MethodDeclaration.class)) {
            symbols.add(method.getNameAsString());
        }

        // Imports (fully-qualified)
        for (ImportDeclaration imp : cu.findAll(ImportDeclaration.class)) {
            symbols.add(imp.getNameAsString());
        }

        return new ArrayList<>(symbols);
    }
}