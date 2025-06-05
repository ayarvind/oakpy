package org.example.compiler.ast;

import java.util.List;

public class WhileStatement implements StatementNode {
    private final ExpressionNode condition;
    private final List<StatementNode> body;

    public WhileStatement(ExpressionNode condition, List<StatementNode> body) {
        this.condition = condition;
        this.body = body;
    }

    public ExpressionNode getCondition() {
        return condition;
    }

    public List<StatementNode> getBody() {
        return body;
    }
}
