package org.example.compiler.ast;

public class NumberLiteral implements ExpressionNode {
    public final int value;

    public NumberLiteral(int value) {
        this.value = value;
    }
}