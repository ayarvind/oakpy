package org.example.compiler.ast;

import java.util.List;

public class FunctionCall implements ExpressionNode {
    private final String functionName;
    private final List<ExpressionNode> arguments;

    public FunctionCall(String functionName, List<ExpressionNode> arguments) {
        this.functionName = functionName;
        this.arguments = arguments;
    }

    public String functionName() { return functionName; }
    public List<ExpressionNode> arguments() { return arguments; }
}
