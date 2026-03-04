package com.mybsam12138.review.configuration;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;

@Component
public class JavaParserProvider {

    private final JavaParser parser;

    public JavaParserProvider() {

        ParserConfiguration config = new ParserConfiguration()
                .setLanguageLevel(ParserConfiguration.LanguageLevel.BLEEDING_EDGE);

        this.parser = new JavaParser(config);
    }

    public CompilationUnit parse(File file) throws FileNotFoundException {

        return parser.parse(file)
                .getResult()
                .orElseThrow(() ->
                        new RuntimeException("Failed to parse file: " + file.getAbsolutePath()));
    }
}