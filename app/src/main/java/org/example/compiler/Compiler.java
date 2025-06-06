package org.example.compiler;

import org.example.compiler.ast.ClassDeclaration;
import org.example.compiler.ast.StatementNode;
import org.example.compiler.interpreter.Interpreter;
import org.example.compiler.parser.Parser;
import org.example.errors.ErrorHandler;
import org.example.compiler.token.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class Compiler {
    private final String oakFileName;
    private final String command;
    private String oakCode;
    private final ErrorHandler errorHandler = new ErrorHandler();

    public Compiler(String oakFileName, String command) {
        this.command = command;
        this.oakFileName = oakFileName;
        System.out.println("Working directory: " + System.getProperty("user.dir"));
        File oakFile = new File(oakFileName);

        if (oakFile.canRead()) {
            try {
                oakCode = Files.readString(oakFile.toPath());
            } catch (IOException e) {
                errorHandler.addFileReadError("Unable to read file: " + e.getMessage());
            }
        } else {
            errorHandler.addFileReadError("File does not exist or cannot be read: " + oakFileName);
        }

        if (errorHandler.hasErrors()) {
            errorHandler.printErrors();
        }
    }

    public boolean canCompile() {
        return oakCode != null && !errorHandler.hasErrors();
    }

    public void compile() {
        if (canCompile()) {
            // 1. Tokenize
            Lexer lexer = new Lexer(oakCode);
            List<Token> tokens = lexer.tokenize();
            // print tokens for debugging
            // print all tokens
            // for (Token token : tokens) {
            //     System.out.println(token);
            // }

            // 2. Parse to AST

            // try {
                Parser parser = new Parser(tokens);
                ClassDeclaration classNode = parser.parseClass();

                // 3. Interpret the AST
                Interpreter interpreter = new Interpreter();
                interpreter.executeProgram(List.of(classNode));
            // } catch (Exception e) {
            //     System.out.println(e.getMessage());
            // }
        } else {
            System.err.println("Cannot compile: file was not read successfully.");
        }
    }
}