package org.example.compiler.ast;

public class BinaryExpression implements ExpressionNode {
    public final ExpressionNode left;
    public final String operator;
    public final ExpressionNode right;

    public BinaryExpression(ExpressionNode left, String operator, ExpressionNode right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    public ExpressionNode left(){
        return this.left;
    }
    public ExpressionNode right(){
        return this.right;
    }
    public String operator(){
        return this.operator;
    }
}
