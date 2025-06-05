package org.example.compiler.ast;

public class ReturnStatement implements StatementNode {
    private final ExpressionNode value;

    public ReturnStatement(ExpressionNode value) {
        this.value = value;
    }

    public ExpressionNode value() {
        return value;
    }
}
