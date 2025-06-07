package org.example.compiler.ast;

import java.util.List;

public class NewObjectExpression implements ExpressionNode {
    private final String className;
    private final List<ExpressionNode> arguments;

    public NewObjectExpression(String className, List<ExpressionNode> arguments) {
        this.className = className;
        this.arguments = arguments;
    }

    public String getClassName() {
        return className;
    }

    public List<ExpressionNode> getArguments() {
        return arguments;
    }
}
