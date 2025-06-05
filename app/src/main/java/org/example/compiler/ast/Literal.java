package org.example.compiler.ast;

public class Literal implements ExpressionNode {
    public final Object value;

    public Literal(Object value) {
        this.value = value;
    }
    public Object value(){
        return this.value;
    }
}
