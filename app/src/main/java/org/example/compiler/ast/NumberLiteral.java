package org.example.compiler.ast;

public class NumberLiteral implements ExpressionNode {
    public final Number value;

    public NumberLiteral(Number numberValue) {
        this.value = numberValue;
    }
}