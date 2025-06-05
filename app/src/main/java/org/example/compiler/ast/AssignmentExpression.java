package org.example.compiler.ast;

public class AssignmentExpression implements ExpressionNode {
    private final String variableName;
    private final String operator; // "+", "-", "*", "/", "%" etc.
    private final ExpressionNode right;
    private final boolean isCompound;

    public AssignmentExpression(String variableName, String operator, ExpressionNode right, boolean isCompound) {
        this.variableName = variableName;
        this.operator = operator;
        this.right = right;
        this.isCompound = isCompound;
    }

    public String name() {
        return variableName;
    }

    public String operator() {
        return operator;
    }

    public ExpressionNode right() {
        return right;
    }

    public boolean isCompound() {
        return isCompound;
    }
}

