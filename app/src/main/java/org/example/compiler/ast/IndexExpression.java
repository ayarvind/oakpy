package org.example.compiler.ast;
import org.example.compiler.ast.ExpressionNode;
/**
 * Represents an index expression in the abstract syntax tree (AST).
 * This is used for accessing elements in arrays or collections.
 */
public class IndexExpression implements ExpressionNode {
    private final ExpressionNode target;
    private final ExpressionNode index;

    public IndexExpression(ExpressionNode target, ExpressionNode index) {
        this.target = target;
        this.index = index;
    }

    public ExpressionNode getTarget() {
        return target;
    }

    public ExpressionNode getIndex() {
        return index;
    }
}
