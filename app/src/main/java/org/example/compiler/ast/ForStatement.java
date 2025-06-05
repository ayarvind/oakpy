package org.example.compiler.ast;

import java.util.List;

public class ForStatement implements StatementNode {
    // For 1: "for (init; condition; increment) { body }"
    private final StatementNode init; // can be VarDeclaration or ExpressionStatement or null
    private final ExpressionNode condition;
    private final ExpressionNode increment; // e.g. i++
    private final List<StatementNode> body;

    // OR For 2: "for (String var : iterable) { body }"
    private final String varName;
    private final ExpressionNode iterable; // e.g. list, array, string
    private final boolean isForEach;

    // Constructor for classic for-loop
    public ForStatement(StatementNode init, ExpressionNode condition, ExpressionNode increment, List<StatementNode> body) {
        this.init = init;
        this.condition = condition;
        this.increment = increment;
        this.body = body;
        this.varName = null;
        this.iterable = null;
        this.isForEach = false;
    }

    // Constructor for foreach loop
    public ForStatement(String varName, ExpressionNode iterable, List<StatementNode> body) {
        this.varName = varName;
        this.iterable = iterable;
        this.body = body;
        this.init = null;
        this.condition = null;
        this.increment = null;
        this.isForEach = true;
    }

    public boolean isForEach() {
        return isForEach;
    }

    public StatementNode getInit() {
        return init;
    }

    public ExpressionNode getCondition() {
        return condition;
    }

    public ExpressionNode getIncrement() {
        return increment;
    }

    public List<StatementNode> getBody() {
        return body;
    }

    public String getVarName() {
        return varName;
    }

    public ExpressionNode getIterable() {
        return iterable;
    }
}
