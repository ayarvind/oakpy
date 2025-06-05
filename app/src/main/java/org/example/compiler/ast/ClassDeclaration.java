package org.example.compiler.ast;

import java.util.List;

public class ClassDeclaration implements StatementNode {
    private final String name;
    private final List<StatementNode> body;

    public ClassDeclaration(String name, List<StatementNode> body) {
        this.name = name;
        this.body = body;
    }

    public String name() {
        return name;
    }

    public List<StatementNode> body() {
        return body;
    }
}
