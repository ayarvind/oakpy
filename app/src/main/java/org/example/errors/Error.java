package org.example.errors;

public abstract class Error {
    protected String message;
    protected int line;

    public Error(String message) {
        this.message = message;
        this.line = -1;
    }

    public Error(String message, int line) {
        this.message = message;
        this.line = line;
    }

    public String getMessage() {
        return message;
    }

    public int getLine() {
        return line;
    }

    public abstract String getType();

    @Override
    public String toString() {
        return "[" + getType() + "] " + (line >= 0 ? "Line " + line + ": " : "") + message;
    }
}
