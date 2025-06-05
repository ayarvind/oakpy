package org.example.compiler.ast;

public class UnaryExpression implements ExpressionNode {
    public final String operator;
    public final ExpressionNode operand;

    public UnaryExpression(String operator, ExpressionNode operand) {
        this.operator = operator;
        this.operand = operand;
    }
    public String operator() {
        return this.operator;
    }
    public ExpressionNode operand() {
        return this.operand;
    }
    @Override
    public String toString() {
        return operator + operand.toString();
    }
}
