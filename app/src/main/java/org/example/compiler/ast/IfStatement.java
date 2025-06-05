package org.example.compiler.ast;

import java.util.List;
import java.util.Objects;

public class IfStatement implements StatementNode {
    private final ExpressionNode condition;
    private final List<StatementNode> thenBranch;
    private final List<ElseIfBranch> elseIfBranches;
    private final List<StatementNode> elseBranch;

    public IfStatement(ExpressionNode condition,
                       List<StatementNode> thenBranch,
                       List<ElseIfBranch> elseIfBranches,
                       List<StatementNode> elseBranch) {
        this.condition = Objects.requireNonNull(condition);
        this.thenBranch = Objects.requireNonNull(thenBranch);
        this.elseIfBranches = elseIfBranches;  // can be null or empty
        this.elseBranch = elseBranch;          // can be null or empty
    }

    public ExpressionNode getCondition() {
        return condition;
    }

    public List<StatementNode> getThenBranch() {
        return thenBranch;
    }

    public List<ElseIfBranch> getElseIfBranches() {
        return elseIfBranches;
    }

    public List<StatementNode> getElseBranch() {
        return elseBranch;
    }
}
