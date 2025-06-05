package org.example.errors;

public class FileReadError extends Error {
    public FileReadError(String message) {
        super(message);
    }
    @Override
    public String getType() {
        return "FileReadError";
    }
}
