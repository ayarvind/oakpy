package org.example.compiler.ast;

public class Return extends RuntimeException {
    private final Object value;

    public Return(Object value) {
        super(null, null, false, false); // disables stack trace for performance
        this.value = value;
    }

    public Object getValue() {
        return value;
    }
}
