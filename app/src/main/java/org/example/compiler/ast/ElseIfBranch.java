package org.example.compiler.ast;

import java.util.List;
import java.util.Objects;

public class ElseIfBranch {
    private final ExpressionNode condition;
    private final List<StatementNode> body;

    public ElseIfBranch(ExpressionNode condition, List<StatementNode> body) {
        this.condition = Objects.requireNonNull(condition);
        this.body = Objects.requireNonNull(body);
    }

    public ExpressionNode getCondition() {
        return condition;
    }

    public List<StatementNode> getBody() {
        return body;
    }
}
