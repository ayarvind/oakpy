package org.example.compiler;

import org.example.compiler.token.Token;
import org.example.compiler.token.TokenType;
import org.example.compiler.token.Tokens;

import java.util.ArrayList;
import java.util.List;

public class Lexer {
    private final String code;
    private int pos = 0;
    private int line = 1;

    private final List<Token> tokens = new ArrayList<>();

    public Lexer(String code) {
        this.code = code;
    }

    public List<Token> tokenize() {
        while (pos < code.length()) {
            char current = peek();

            if (Character.isWhitespace(current)) {
                if (current == '\n') line++;
                advance();
            } else if (Character.isLetter(current) || current == '_') {
                tokenizeIdentifierOrKeyword();
            } else if (Character.isDigit(current)) {
                tokenizeNumber();
            } else if (current == '"') {
                tokenizeString();
            } else if (current == '/' && peekNext() == '/') {
                skipLineComment();
            } else if (Tokens.isDelimiter(current)) {
                tokens.add(new Token(TokenType.DELIMITER, String.valueOf(current), line));
                advance();
            } else if (isOperatorStart(current)) {
                tokenizeOperator();
            } else {
                System.err.println("Unknown character at line " + line + ": '" + current + "'");
                advance();
            }
        }

        return tokens;
    }

    private void tokenizeIdentifierOrKeyword() {
        StringBuilder sb = new StringBuilder();
        while (pos < code.length() && (Character.isLetterOrDigit(peek()) || peek() == '_')) {
            sb.append(peek());
            advance();
        }

        String word = sb.toString();
        if (Tokens.isKeyword(word)) {
            tokens.add(new Token(TokenType.KEYWORD, word, line));
        } else {
            tokens.add(new Token(TokenType.IDENTIFIER, word, line));
        }
    }

    private void tokenizeNumber() {
        StringBuilder sb = new StringBuilder();
        while (pos < code.length() && Character.isDigit(peek())) {
            sb.append(peek());
            advance();
        }
        tokens.add(new Token(TokenType.NUMBER, sb.toString(), line));
    }

    private void tokenizeString() {
        advance(); // Skip opening quote
        StringBuilder sb = new StringBuilder();
        while (pos < code.length() && peek() != '"') {
            if (peek() == '\n') line++;
            sb.append(peek());
            advance();
        }
        advance(); // Skip closing quote
        tokens.add(new Token(TokenType.STRING, sb.toString(), line));
    }

    private void skipLineComment() {
        while (pos < code.length() && peek() != '\n') {
            advance();
        }
        line++;
        advance(); // skip newline
    }

    private void tokenizeOperator() {
        StringBuilder sb = new StringBuilder();
        sb.append(peek());
        advance();

        if (pos < code.length()) {
            sb.append(peek());
            if (Tokens.isOperator(sb.toString())) {
                advance();
                tokens.add(new Token(TokenType.OPERATOR, sb.toString(), line));
                return;
            }
            sb.setLength(1); // fallback to single-char operator
        }

        tokens.add(new Token(TokenType.OPERATOR, sb.toString(), line));
    }

    private char peek() {
        return code.charAt(pos);
    }

    private char peekNext() {
        return pos + 1 < code.length() ? code.charAt(pos + 1) : '\0';
    }

    private void advance() {
        pos++;
    }

    private boolean isOperatorStart(char c) {
        return "+-*/%=!<>&|:".indexOf(c) != -1;
    }
}
