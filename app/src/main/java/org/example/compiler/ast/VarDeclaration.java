package org.example.compiler.ast;

public class VarDeclaration implements StatementNode {
    private final String name;
    private final ExpressionNode value;

    public VarDeclaration(String name, ExpressionNode value) {
        this.name = name;
        this.value = value;
    }

    public String name() {
        return name;
    }

    public ExpressionNode value() {
        return value;
    }
}
