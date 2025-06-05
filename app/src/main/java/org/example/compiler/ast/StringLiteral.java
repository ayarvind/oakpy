package org.example.compiler.ast;

public class StringLiteral implements ExpressionNode {
    public final String value;

    public StringLiteral(String value) {
        this.value = value;
    }
}
