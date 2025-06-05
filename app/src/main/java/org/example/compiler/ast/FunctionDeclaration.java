package org.example.compiler.ast;

import java.util.List;

public class FunctionDeclaration implements StatementNode {
    private final String name;
    private final List<String> parameters;           // New
    private final List<StatementNode> body;

    public FunctionDeclaration(String name, List<String> parameters, List<StatementNode> body) {
        this.name = name;
        this.parameters = parameters;
        this.body = body;
    }

    public String name() { return name; }
    public List<String> parameters() { return parameters; }
    public List<StatementNode> body() { return body; }
}
