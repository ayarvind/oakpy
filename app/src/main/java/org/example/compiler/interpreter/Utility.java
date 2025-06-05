package org.example.compiler.interpreter;

public class Utility {
    public static boolean isTruthy(Object value) {
        if (value == null) {
            return false;
        }
        if (value instanceof Boolean b) {
            return b;
        }
        if (value instanceof Integer i) {
            return i != 0;
        }
        if (value instanceof Double d) {
            return d != 0.0;
        }
        if (value instanceof String s) {
            return !s.isEmpty();
        }
        // For any other object, consider it truthy (like JavaScript)
        return true;
    }

}
