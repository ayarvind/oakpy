package org.example.compiler.ast;

public class ThisExpression implements ExpressionNode {
    @Override
    public String toString() {
        return "this";
    }
}
