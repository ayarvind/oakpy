package org.example.compiler.ast;

public class CompoundAssignmentExpression implements ExpressionNode {
    public final String variableName;
    public final String operator; // e.g., "+="
    public final ExpressionNode value;

    public CompoundAssignmentExpression(String variableName, String operator, ExpressionNode value) {
        this.variableName = variableName;
        this.operator = operator;
        this.value = value;
    }
}
