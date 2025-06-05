package org.example.compiler.parser;

import org.example.compiler.token.Token;
import org.example.compiler.token.TokenType;
import org.example.compiler.ast.*;

import java.util.ArrayList;
import java.util.List;

public class Parser {
    private final List<Token> tokens;
    private int current = 0;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public ClassDeclaration parseClass() {
        consume(TokenType.KEYWORD, "class");
        String className = consume(TokenType.IDENTIFIER).getValue();
        consume(TokenType.DELIMITER, "{");

        List<StatementNode> members = new ArrayList<>();
        while (!check(TokenType.DELIMITER, "}")) {
            members.add(parseStatement());
        }

        consume(TokenType.DELIMITER, "}");
        return new ClassDeclaration(className, members);
    }

    private StatementNode parseStatement() {
        if (match(TokenType.KEYWORD, "var")) {
            String name = consume(TokenType.IDENTIFIER).getValue();
            consume(TokenType.OPERATOR, "=");
            ExpressionNode value = parseExpression();
            consume(TokenType.DELIMITER, ";");
            return new VarDeclaration(name, value);
        }

        if (match(TokenType.KEYWORD, "print")) {
            consume(TokenType.DELIMITER, "(");
            ExpressionNode value = parseExpression();
            consume(TokenType.DELIMITER, ")");
            consume(TokenType.DELIMITER, ";");
            return new PrintStatement(value);
        }

        if (match(TokenType.KEYWORD, "def")) {
            String name = consume(TokenType.IDENTIFIER).getValue();
            consume(TokenType.DELIMITER, "(");
            List<String> parameters = new ArrayList<>();
            if (!check(TokenType.DELIMITER, ")")) {
                do {
                    parameters.add(consume(TokenType.IDENTIFIER).getValue());
                } while (match(TokenType.DELIMITER, ","));
            }
            consume(TokenType.DELIMITER, ")");

            consume(TokenType.DELIMITER, "{");
            List<StatementNode> body = new ArrayList<>();
            while (!check(TokenType.DELIMITER, "}")) {
                body.add(parseStatement());
            }
            consume(TokenType.DELIMITER, "}");
            return new FunctionDeclaration(name, parameters, body);
        }

        // Check for expression as a statement (e.g., greet();)
        ExpressionNode expr = parseExpression();
        consume(TokenType.DELIMITER, ";");
        return new ExpressionStatement(expr);
    }

    private ExpressionNode parseExpression() {
        return parseCall();
    }

    private ExpressionNode parseCall() {
        ExpressionNode expr = parsePrimary();

        while (true) {
            if (match(TokenType.DELIMITER, "(")) {
                List<ExpressionNode> args = new ArrayList<>();
                if (!check(TokenType.DELIMITER, ")")) {
                    do {
                        args.add(parseExpression());
                    } while (match(TokenType.DELIMITER, ","));
                }
                consume(TokenType.DELIMITER, ")");
                expr = new FunctionCall(((VariableReference) expr).name, args);
            } else {
                break;
            }
        }

        return expr;
    }

    private ExpressionNode parsePrimary() {
        if (match(TokenType.NUMBER)) {
            return new NumberLiteral(Integer.parseInt(previous().getValue()));
        }

        if (match(TokenType.STRING)) {
            return new StringLiteral(previous().getValue());
        }

        if (match(TokenType.IDENTIFIER)) {
            return new VariableReference(previous().getValue());
        }

        throw error(peek(), "Expected an expression");
    }

    // ----- Utility Methods -----

    private boolean match(TokenType type) {
        if (check(type)) {
            advance();
            return true;
        }
        return false;
    }

    private boolean match(TokenType type, String value) {
        if (check(type, value)) {
            advance();
            return true;
        }
        return false;
    }

    private boolean check(TokenType type) {
        if (isAtEnd()) return false;
        return peek().getType() == type;
    }

    private boolean check(TokenType type, String value) {
        if (isAtEnd()) return false;
        Token token = peek();
        return token.getType() == type && token.getValue().equals(value);
    }

    private Token consume(TokenType type) {
        if (check(type)) return advance();
        throw error(peek(), "Expected token of type " + type);
    }

    private Token consume(TokenType type, String value) {
        if (check(type, value)) return advance();
        throw error(peek(), "Expected token " + value);
    }

    private Token advance() {
        if (!isAtEnd()) current++;
        return previous();
    }

    private boolean isAtEnd() {
        return current >= tokens.size();
    }

    private Token peek() {
        return tokens.get(current);
    }

    private Token previous() {
        return tokens.get(current - 1);
    }

    private RuntimeException error(Token token, String message) {
        return new RuntimeException("[Parser Error] Line " + token.getLine() + ": " + message);
    }
}
