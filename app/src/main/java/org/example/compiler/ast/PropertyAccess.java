package org.example.compiler.ast;
import org.example.compiler.ast.ExpressionNode;
/**
 * Represents a property access in the abstract syntax tree (AST).
 * This is used for accessing properties of objects.
 */
public class PropertyAccess implements ExpressionNode {
    public final ExpressionNode target;
    public final String property;

    public PropertyAccess(ExpressionNode target, String property) {
        this.target = target;
        this.property = property;
    }
}