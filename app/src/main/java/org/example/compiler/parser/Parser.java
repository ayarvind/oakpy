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
        if (checkKeyword("if")) {
            return parseIfStatement();
        }

        if (checkKeyword("while")) {
            return parseWhileStatement();
        }

        if (checkKeyword("for")) {
            return parseForStatement();
        }
        if(match(TokenType.KEYWORD, "break")) {
            consume(TokenType.DELIMITER, ";");
            return (StatementNode) new BreakStatement();
        }
        if (match(TokenType.KEYWORD, "continue")) {
            consume(TokenType.DELIMITER, ";");
            return (StatementNode) new ContinueStatement();
        }

        if (match(TokenType.KEYWORD, "var")) {
            String name = consume(TokenType.IDENTIFIER).getValue();
            consume(TokenType.OPERATOR, "=");
            ExpressionNode value = parseExpression();
            consume(TokenType.DELIMITER, ";");
            return new VarDeclaration(name, value);
        }

        if (match(TokenType.KEYWORD, "return")) {
            ExpressionNode returnValue = null;
            if (!check(TokenType.DELIMITER, ";")) {
                returnValue = parseExpression();
            }
            consume(TokenType.DELIMITER, ";");
            return new ReturnStatement(returnValue);
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

        // Fallback: any valid expression (assignments, function calls, ++/--, etc.)
        ExpressionNode expr = parseExpression();
        consume(TokenType.DELIMITER, ";");
        return new ExpressionStatement(expr);
    }

    private StatementNode parseWhileStatement() {
        consume(TokenType.KEYWORD, "while"); // consume 'while'
        consume(TokenType.DELIMITER, "("); // consume '('

        ExpressionNode condition = parseExpression();

        consume(TokenType.DELIMITER, ")"); // consume ')'

        List<StatementNode> body = parseBlock();

        return new WhileStatement(condition, body);
    }

    private StatementNode parseForStatement() {
        consume(TokenType.KEYWORD, "for"); // consume 'for'
        consume(TokenType.DELIMITER, "("); // consume '('

        StatementNode init = null;
        if (!check(TokenType.DELIMITER, ";")) {
            if (match(TokenType.KEYWORD, "var")) {
                // parse var declaration without consuming trailing semicolon here
                String name = consume(TokenType.IDENTIFIER).getValue();
                consume(TokenType.OPERATOR, "=");
                ExpressionNode value = parseExpression();
                // do NOT consume the semicolon here
                init = new VarDeclaration(name, value);
            } else {
                // parse expression statement without trailing semicolon
                ExpressionNode expr = parseExpression();
                init = new ExpressionStatement(expr);
            }
        }
        consume(TokenType.DELIMITER, ";");

        ExpressionNode condition = null;
        if (!check(TokenType.DELIMITER, ";")) {
            condition = parseExpression();
        }
        consume(TokenType.DELIMITER, ";");

        ExpressionNode increment = null;
        if (!check(TokenType.DELIMITER, ")")) {
            increment = parseExpression();
        }
        consume(TokenType.DELIMITER, ")");

        List<StatementNode> body = parseBlock();

        return new ForStatement(init, condition, increment, body);
    }
    // ---- Expression Parsing ----

    private ExpressionNode parseExpression() {
        return parseAssignment();
    }

    private ExpressionNode parseLogicalOr() {
        ExpressionNode expr = parseLogicalAnd();
        while (match(TokenType.OPERATOR, "||")) {
            String operator = previous().getValue();
            ExpressionNode right = parseLogicalAnd();
            expr = new BinaryExpression(expr, operator, right);
        }
        return expr;
    }

    private ExpressionNode parseLogicalAnd() {
        ExpressionNode expr = parseBitwiseOr();
        while (match(TokenType.OPERATOR, "&&")) {
            String operator = previous().getValue();
            ExpressionNode right = parseBitwiseOr();
            expr = new BinaryExpression(expr, operator, right);
        }
        return expr;
    }

    private ExpressionNode parseBitwiseOr() {
        ExpressionNode expr = parseBitwiseXor();
        while (match(TokenType.OPERATOR, "|")) {
            String operator = previous().getValue();
            ExpressionNode right = parseBitwiseXor();
            expr = new BinaryExpression(expr, operator, right);
        }
        return expr;
    }

    private ExpressionNode parseBitwiseXor() {
        ExpressionNode expr = parseBitwiseAnd();
        while (match(TokenType.OPERATOR, "^")) {
            String operator = previous().getValue();
            ExpressionNode right = parseBitwiseAnd();
            expr = new BinaryExpression(expr, operator, right);
        }
        return expr;
    }

    private ExpressionNode parseBitwiseAnd() {
        ExpressionNode expr = parseEquality();
        while (match(TokenType.OPERATOR, "&")) {
            String operator = previous().getValue();
            ExpressionNode right = parseEquality();
            expr = new BinaryExpression(expr, operator, right);
        }
        return expr;
    }

    private ExpressionNode parseEquality() {
        ExpressionNode expr = parseComparison();
        while (match(TokenType.OPERATOR, "==") || match(TokenType.OPERATOR, "!=")) {
            String operator = previous().getValue();
            ExpressionNode right = parseComparison();
            expr = new BinaryExpression(expr, operator, right);
        }
        return expr;
    }

    private ExpressionNode parseComparison() {
        ExpressionNode expr = parseShift();
        while (match(TokenType.OPERATOR, "<") || match(TokenType.OPERATOR, ">") ||
                match(TokenType.OPERATOR, "<=") || match(TokenType.OPERATOR, ">=")) {
            String operator = previous().getValue();
            ExpressionNode right = parseShift();
            expr = new BinaryExpression(expr, operator, right);
        }
        return expr;
    }

    private ExpressionNode parseShift() {
        ExpressionNode expr = parseAddition();
        while (match(TokenType.OPERATOR, "<<") || match(TokenType.OPERATOR, ">>") || match(TokenType.OPERATOR, ">>>")) {
            String operator = previous().getValue();
            ExpressionNode right = parseAddition();
            expr = new BinaryExpression(expr, operator, right);
        }
        return expr;
    }

    private ExpressionNode parseAddition() {
        ExpressionNode expr = parseMultiplication();
        while (match(TokenType.OPERATOR, "+") || match(TokenType.OPERATOR, "-")) {
            String operator = previous().getValue();
            ExpressionNode right = parseMultiplication();
            expr = new BinaryExpression(expr, operator, right);
        }
        return expr;
    }

    private ExpressionNode parseMultiplication() {
        ExpressionNode expr = parseExponent();
        // while (match(TokenType.OPERATOR, "*", "/", "%", "//")) {
        //     String operator = previous().getValue();
        //     ExpressionNode right = parseExponent();
        //     expr = new BinaryExpression(expr, operator, right);
        // }
        while (match(TokenType.OPERATOR, "*") || match(TokenType.OPERATOR, "/") || match(TokenType.OPERATOR, "%") || match(TokenType.OPERATOR, "//")) {
            String operator = previous().getValue();
            ExpressionNode right = parseExponent();
            expr = new BinaryExpression(expr, operator, right);
        }
        return expr;
    }

    private ExpressionNode parseExponent() {
        ExpressionNode expr = parseUnary();
        while (match(TokenType.OPERATOR, "**")) {
            String operator = previous().getValue();
            ExpressionNode right = parseExponent(); // right-associative, call itself
            expr = new BinaryExpression(expr, operator, right);
        }
        return expr;
    }

    private ExpressionNode parseUnary() {
        if (match("+", "-", "!", "~", "++", "--")) {
            String operator = previous().getValue();
            ExpressionNode right = parseUnary();
            if (operator.equals("++") || operator.equals("--")) {
                if (!(right instanceof VariableReference)) {
                    throw error(previous(), "Increment/Decrement must be on a variable");
                }
                VariableReference ref = (VariableReference) right;
                return new IncrementExpression(ref.name(), operator, true); // prefix
            }
            return new UnaryExpression(operator, right);
        }
        return parsePostfix();
    }

    private ExpressionNode parsePostfix() {
        ExpressionNode expr = parseCall();
        while (match("++", "--")) {
            String operator = previous().getValue();
            if (!(expr instanceof VariableReference ref)) {
                throw error(previous(), "Increment/Decrement must be on a variable");
            }
            expr = new IncrementExpression(ref.name(), operator, false); // postfix
        }
        return expr;
    }

    private ExpressionNode parseAssignment() {
        ExpressionNode expr = parseLogicalOr();

        if (match("=", "+=", "-=", "*=", "/=", "%=", "&=", "|=", "^=", "<<=", ">>=", ">>>=")) {
            String operator = previous().getValue();
            ExpressionNode value = parseAssignment();

            if (!(expr instanceof VariableReference ref)) {
                throw error(previous(), "Assignment must be to a variable");
            }

            if (operator.equals("=")) {
                return new AssignmentExpression(ref.name(), operator, value, false);
            } else {
                // compound assignment like +=
                return new CompoundAssignmentExpression(ref.name(), operator, value);
            }
        }

        return expr;
    }

    public IfStatement parseIfStatement() {
        consume(TokenType.KEYWORD, "if", "Expected 'if' keyword");

        // Parse condition inside parentheses
        consume(TokenType.DELIMITER, "(", "Expected '(' after 'if'");
        ExpressionNode condition = parseExpression();
        consume(TokenType.DELIMITER, ")", "Expected ')' after if condition");

        // Parse then block
        List<StatementNode> thenBranch = parseBlock();

        // Parse else-if branches
        List<ElseIfBranch> elseIfBranches = new ArrayList<>();
        while (checkKeyword("else")) {
            // peek ahead to see if 'else if' or just 'else'
            int savedPosition = current;
            advance(); // consume 'else'

            if (checkKeyword("if")) {
                advance(); // consume 'if'
                consume(TokenType.DELIMITER, "(", "Expected '(' after 'else if'");
                ExpressionNode elseIfCondition = parseExpression();
                consume(TokenType.DELIMITER, ")", "Expected ')' after else if condition");
                List<StatementNode> elseIfBody = parseBlock();

                elseIfBranches.add(new ElseIfBranch(elseIfCondition, elseIfBody));
            } else {
                // Not 'else if', so restore position and break
                current = savedPosition;
                break;
            }
        }

        // Parse else branch
        List<StatementNode> elseBranch = null;
        if (checkKeyword("else")) {
            advance(); // consume 'else'
            elseBranch = parseBlock();
        }

        return new IfStatement(condition, thenBranch, elseIfBranches, elseBranch);
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

        if (match(TokenType.DELIMITER, "(")) {
            ExpressionNode expr = parseExpression();
            consume(TokenType.DELIMITER, ")");
            return expr;
        }

        throw error(peek(), "Expected an expression");
    }

    // ---- Utility Methods ----

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

    private boolean match(String... values) {
        for (String value : values) {
            if (check(TokenType.OPERATOR, value)) {
                advance();
                return true;
            }
        }
        return false;
    }

    private boolean check(TokenType type) {
        if (isAtEnd())
            return false;
        return peek().getType() == type;
    }

    private boolean check(TokenType type, String value) {
        if (isAtEnd())
            return false;
        Token token = peek();
        return token.getType() == type && token.getValue().equals(value);
    }

    private List<StatementNode> parseBlock() {
        consume(TokenType.DELIMITER, "{", "Expected '{' to start block");
        List<StatementNode> statements = new ArrayList<>();
        while (!checkDelimiter("}")) {
            statements.add(parseStatement());
        }
        consume(TokenType.DELIMITER, "}", "Expected '}' to end block");
        return statements;
    }

    private boolean checkDelimiter(String delimiter) {
        Token token = currentToken();
        return token != null && token.getType() == TokenType.DELIMITER && token.getValue().equals(delimiter);
    }

    private Token consume(TokenType type) {
        if (check(type))
            return advance();
        throw error(peek(), "Expected token of type " + type);
    }

    private Token consume(TokenType type, String value) {
        if (check(type, value))
            return advance();
        throw error(peek(), "Expected token " + value);
    }

    private Token consume(TokenType expectedType, String expectedValue, String errorMessage) {
        Token token = currentToken();
        if (token != null && token.getType() == expectedType && token.getValue().equals(expectedValue)) {
            advance();
            return token;
        }
        throw new RuntimeException(errorMessage + " at token " + token);
    }

    private boolean checkKeyword(String keyword) {
        Token token = currentToken();
        return token != null && token.getType() == TokenType.KEYWORD && token.getValue().equals(keyword);
    }

    private Token lookAhead(int n) {
        int index = current + n;
        if (index >= tokens.size()) {
            return tokens.get(tokens.size() - 1); // or return a special EOF token
        }
        return tokens.get(index);
    }

    private Token currentToken() {
        if (current < tokens.size()) {
            return tokens.get(current);
        }
        return null; // Or a special EOF token
    }

    private boolean matchKeyword(String keyword) {
        if (checkKeyword(keyword)) {
            advance();
            return true;
        }
        return false;
    }

    private Token advance() {
        if (!isAtEnd())
            current++;
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
