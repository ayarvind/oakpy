package org.example.errors;

import java.util.ArrayList;
import java.util.List;

public class ErrorHandler {
    private final List<Error> errorList = new ArrayList<>();

    public void addError(Error error) {
        errorList.add(error);
    }

    public void addSyntaxError(String message, int line) {
        errorList.add(new SyntaxError(message, line));
    }

//    public void addSemanticError(String message, int line) {
//        errorList.add(new SemanticError(message, line));
//    }

    public void addFileReadError(String message) {
        errorList.add(new FileReadError(message));
    }

    public boolean hasErrors() {
        return !errorList.isEmpty();
    }

    public void printErrors() {
        for (Error error : errorList) {
            System.err.println(error);
        }
    }

    public List<Error> getErrors() {
        return errorList;
    }

    public void clearErrors() {
        errorList.clear();
    }
}
