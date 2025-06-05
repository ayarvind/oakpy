package org.example.errors;
public class SyntaxError extends Error {
    public SyntaxError(String message, int line) {
        super(message, line);
    }
    @Override
    public String getType() {
        return "SyntaxError";
    }
}
