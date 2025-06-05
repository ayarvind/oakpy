package org.example.compiler.ast;

public class VariableReference implements ExpressionNode {
    public final String name;

    public VariableReference(String name) {
        this.name = name;
    }
    public String name(){
        return this.name;
    }
}
