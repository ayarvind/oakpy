package org.example.compiler.ast;

/**
 * Represents an identifier in the Abstract Syntax Tree.
 * An identifier typically refers to a variable name, function name, or class name.
 * It implements ExpressionNode because identifiers can appear as expressions
 * (e.g., `x` in `print(x)` or `a + b`).
 */
public record Identifier(String name) implements ExpressionNode {
    
}
