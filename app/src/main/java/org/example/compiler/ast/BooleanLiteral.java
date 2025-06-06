package org.example.compiler.ast;

public class BooleanLiteral implements ExpressionNode {
    public final boolean value;

    public BooleanLiteral(boolean value) {
        this.value = value;
    }
}
