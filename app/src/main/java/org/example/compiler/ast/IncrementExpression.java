package org.example.compiler.ast;

public class IncrementExpression implements ExpressionNode {
    private final String variableName;
    private final String operator; // "++" or "--"
    private final boolean isPrefix;

    public IncrementExpression(String variableName, String operator, boolean isPrefix) {
        this.variableName = variableName;
        this.operator = operator;
        this.isPrefix = isPrefix;
    }

    public String variableName() {
        return variableName;
    }

    public String operator() {
        return operator;
    }

    public boolean isPrefix() {
        return isPrefix;
    }
}
