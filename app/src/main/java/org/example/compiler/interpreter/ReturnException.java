package org.example.compiler.interpreter;

public class ReturnException extends RuntimeException {
    private final Object value;

    public ReturnException(Object value) {
        super(null, null, false, false); // suppress stack trace
        this.value = value;
    }

    public Object getValue() {
        return value;
    }
}
