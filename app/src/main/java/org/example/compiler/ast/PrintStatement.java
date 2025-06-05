package org.example.compiler.ast;

public class PrintStatement implements StatementNode {
    public final ExpressionNode expression;

    public PrintStatement(ExpressionNode expression) {
        this.expression = expression;
    }
    public ExpressionNode expression() {
        return expression;
    }
}
