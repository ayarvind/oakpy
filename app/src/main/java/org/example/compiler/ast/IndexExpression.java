package org.example.compiler.ast;

public class IndexExpression implements ExpressionNode {
    private final ExpressionNode listExpr; 
    private final ExpressionNode indexExpr;
    public IndexExpression(ExpressionNode listExpr, ExpressionNode indexExpr) {
        this.listExpr = listExpr;
        this.indexExpr = indexExpr;
    }

}
