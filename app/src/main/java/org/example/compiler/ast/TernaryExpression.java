package org.example.compiler.ast;

public class TernaryExpression implements ExpressionNode {
    public final ExpressionNode condition;
    public final ExpressionNode trueExpr;
    public final ExpressionNode falseExpr;

    public TernaryExpression(ExpressionNode condition, ExpressionNode trueExpr, ExpressionNode falseExpr) {
        this.condition = condition;
        this.trueExpr = trueExpr;
        this.falseExpr = falseExpr;
    }
}
