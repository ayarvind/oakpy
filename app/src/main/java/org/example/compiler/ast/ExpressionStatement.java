package org.example.compiler.ast;

public class ExpressionStatement implements StatementNode {
    public final ExpressionNode expression;

    public ExpressionStatement(ExpressionNode expression) {
        this.expression = expression;
    }
    public ExpressionNode expression(){
        return this.expression;
    }
}
