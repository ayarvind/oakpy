package org.example.compiler.token;

import java.util.Set;

public class Tokens {

    public static final Set<String> KEYWORDS = Set.of(
            "class", "interface", "const", "var", "def",
            "if", "else", "while", "for", "switch","in",
            "break", "continue", "throw", "try", "catch",
            "true", "false", "null",
            "finally", "import", "export", "new", "this", "super",
            "list","map","set", "tuple",
            "case", "return", "print");

    public static final Set<String> OPERATORS = Set.of(
            "+", "-", "*","**","//", "/", "%", "=", "==", "!=", "<", "<=",
            ">", ">=", "&&", "||", "!", "~", "::", "<<", ">>", ">>>",
            "&", "|", "^",
            "+=", "-=", "*=", "/=", "%=", "&=", "|=", "^=", "<<=", ">>=", ">>>=",
            "++", "--");
    public static final Set<Character> DELIMITERS = Set.of(
            '(', ')', '{', '}', '[', ']', ',', ':', ';','.','?');

    public static boolean isKeyword(String word) {
        return KEYWORDS.contains(word);
    }

    public static boolean isOperator(String op) {
        return OPERATORS.contains(op);
    }

    public static boolean isDelimiter(char c) {
        return DELIMITERS.contains(c);
    }
}
