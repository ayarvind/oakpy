package org.example.compiler.ast;

import java.util.List;

public class ForEachStatement implements StatementNode {
    private final String variableName;
    private final ExpressionNode iterable;
    private final List<StatementNode> body;

    public ForEachStatement(String variableName, ExpressionNode iterable, List<StatementNode> body) {
        this.variableName = variableName;
        this.iterable = iterable;
        this.body = body;
    }

    public String getVariableName() {
        return variableName;
    }

    public ExpressionNode getIterable() {
        return iterable;
    }

    public List<StatementNode> getBody() {
        return body;
    }
}
