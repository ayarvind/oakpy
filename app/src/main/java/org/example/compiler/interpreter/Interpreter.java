package org.example.compiler.interpreter;

import org.example.compiler.ast.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Interpreter {
    private static class ContinueException extends RuntimeException {
    }

    private static class BreakException extends RuntimeException {
    }

    private final Map<String, ClassDeclaration> classes = new HashMap<>();
    private Map<String, Object> locals;

    public void executeProgram(List<StatementNode> programBody) {
        for (StatementNode stmt : programBody) {
            if (stmt instanceof ClassDeclaration cls) {
                classes.put(cls.name(), cls);
            }
        }
        runMain();
    }

    private void runMain() {
        for (ClassDeclaration cls : classes.values()) {
            for (StatementNode member : cls.body()) {
                if (member instanceof FunctionDeclaration func && func.name().equals("main")) {
                    Object result = callMethod(cls.name(), "main");
                    return;
                }
            }
        }
        throw new RuntimeException("No main method found in any class");
    }

    public Object callMethod(String className, String methodName, List<Object> args) {
        ClassDeclaration cls = classes.get(className);
        if (cls == null) {
            throw new RuntimeException("Class not found: " + className);
        }

        for (StatementNode member : cls.body()) {
            if (member instanceof FunctionDeclaration func && func.name().equals(methodName)) {
                return executeFunction(func, className, args);
            }
        }
        throw new RuntimeException("Method " + methodName + " not found in class " + className);
    }

    public Object callMethod(String className, String methodName) {
        return callMethod(className, methodName, List.of());
    }

    private Object executeFunction(FunctionDeclaration func, String currentClassName, List<Object> args) {
        locals = new HashMap<>();

        List<String> paramNames = func.parameters();
        if (args.size() != paramNames.size()) {
            throw new RuntimeException(
                    "Function " + func.name() + " expects " + paramNames.size() + " arguments but got " + args.size());
        }

        for (int i = 0; i < paramNames.size(); i++) {
            locals.put(paramNames.get(i), args.get(i));
        }

        try {
            for (StatementNode stmt : func.body()) {
                executeStatement(stmt, currentClassName);
            }
        } catch (ReturnException r) {
            return r.getValue();
        }

        return null;
    }

    private void executeForLoop(ForStatement forStmt, String currentClassName) {
        if (forStmt.getInit() != null) {
            executeStatement(forStmt.getInit(), currentClassName);
        }

        loop: while (Utility.isTruthy(evaluateExpression(forStmt.getCondition(), currentClassName))) {
            try {
                for (StatementNode stmt : forStmt.getBody()) {
                    try {
                        executeStatement(stmt, currentClassName);
                    } catch (ContinueException ce) {
                        // skip rest of loop body, continue next iteration
                        break;
                    } catch (BreakException be) {
                        // exit the loop entirely
                        break loop;
                    }
                }
            } catch (BreakException be) {
                break;
            }

            if (forStmt.getIncrement() != null) {
                evaluateExpression(forStmt.getIncrement(), currentClassName);
            }
        }
    }

    private void executeStatement(StatementNode stmt, String currentClassName) {
        if (stmt instanceof VarDeclaration varDecl) {
            Object value = evaluateExpression(varDecl.value(), currentClassName);
            locals.put(varDecl.name(), value);

        } else if (stmt instanceof PrintStatement printStmt) {
            Object val = evaluateExpression(printStmt.expression(), currentClassName);
            System.out.println(val);

        } else if (stmt instanceof ExpressionStatement exprStmt) {
            if (exprStmt.expression() instanceof FunctionCall funcCall) {
                callMethod(currentClassName, funcCall.functionName());
            } else {
                evaluateExpression(exprStmt.expression(), currentClassName);
            }

        } else if (stmt instanceof ReturnStatement ret) {
            Object value = null;
            if (ret.value() != null) {
                value = evaluateExpression(ret.value(), currentClassName);
            }
            throw new ReturnException(value);
        } else if (stmt instanceof IfStatement ifStatement) {
            executeIfStatement(ifStatement, currentClassName);
        } else if (stmt instanceof WhileStatement whileStmt) {
            loop: while (Utility.isTruthy(evaluateExpression(whileStmt.getCondition(), currentClassName))) {
                try {
                    executeBlock(whileStmt.getBody(), currentClassName);
                } catch (ContinueException ce) {
                    continue;
                } catch (BreakException be) {
                    break loop;
                }
            }
        } else if (stmt instanceof ForStatement forStmt) {
            executeForLoop(forStmt, currentClassName);
        } else if (stmt instanceof ContinueStatement) {
            throw new ContinueException();
        } else if (stmt instanceof BreakStatement) {
            throw new BreakException();
        }

        else {
            throw new RuntimeException("Unsupported statement: " + stmt.getClass().getSimpleName());
        }
    }

    private Object executeBlock(List<StatementNode> statements, String currentClassName) {
        Object lastResult = null;
        for (StatementNode stmt : statements) {
            executeStatement(stmt, currentClassName);
        }
        return lastResult;
    }

    private Object executeIfStatement(IfStatement ifStmt, String currentClassName) {
        Object conditionValue = evaluateExpression(ifStmt.getCondition(), currentClassName);
        if (!(conditionValue instanceof Boolean)) {
            throw new RuntimeException("Condition must evaluate to a boolean");
        }

        if ((Boolean) conditionValue) {
            for (StatementNode stmt : ifStmt.getThenBranch()) {
                executeStatement(stmt, currentClassName);
            }
        } else {
            for (ElseIfBranch elseIf : ifStmt.getElseIfBranches()) {
                Object elseIfCondition = evaluateExpression(elseIf.getCondition(), currentClassName);
                if (!(elseIfCondition instanceof Boolean)) {
                    throw new RuntimeException("Else-if condition must evaluate to a boolean");
                }
                if ((Boolean) elseIfCondition) {
                    for (StatementNode stmt : elseIf.getBody()) {
                        executeStatement(stmt, currentClassName);
                    }
                    return null;
                }
            }

            if (ifStmt.getElseBranch() != null) {
                for (StatementNode stmt : ifStmt.getElseBranch()) {
                    executeStatement(stmt, currentClassName);
                }
            }
        }
        return null;
    }

    private Object evaluateExpression(ExpressionNode expr, String currentClassName) {
        if (expr instanceof NumberLiteral number) {
            return number.value;

        } else if (expr instanceof StringLiteral string) {
            return string.value;

        } else if (expr instanceof Identifier ident) {
            if (locals.containsKey(ident.name())) {
                return locals.get(ident.name());
            } else {
                throw new RuntimeException("Undefined variable: " + ident.name());
            }

        } else if (expr instanceof VariableReference varRef) {
            if (locals.containsKey(varRef.name())) {
                return locals.get(varRef.name());
            } else {
                throw new RuntimeException("Undefined variable: " + varRef.name());
            }

        } else if (expr instanceof BinaryExpression binExpr) {
            Object left = evaluateExpression(binExpr.left, currentClassName);
            Object right = evaluateExpression(binExpr.right, currentClassName);
            String op = binExpr.operator;

            if (left instanceof Integer l && right instanceof Integer r) {
                return switch (op) {
                    case "+" -> l + r;
                    case "-" -> l - r;
                    case "*" -> l * r;
                    case "/" -> {
                        if (r == 0)
                            throw new ArithmeticException("Division by zero");
                        yield l / r;
                    }
                    case "**" -> {
                        if (r < 0)
                            throw new ArithmeticException("Negative exponent not supported for integers");
                        yield (int) Math.pow(l, r);
                    }

                    case "%" -> {
                        if (r == 0)
                            throw new ArithmeticException("Modulus by zero");
                        yield l % r;
                    }
                    case "&" -> l & r;
                    case "|" -> l | r;
                    case "^" -> l ^ r;
                    case "<<" -> l << r;
                    case ">>" -> l >> r;
                    case ">>>" -> l >>> r;
                    case "==" -> l == r;
                    case "!=" -> l != r;
                    case ">" -> l > r;
                    case "<" -> l < r;
                    case ">=" -> l >= r;
                    case "<=" -> l <= r;
                    default -> throw new RuntimeException("Unsupported operator for integers: " + op);
                };

            } else if (left instanceof Boolean bl && right instanceof Boolean br) {
                return switch (op) {
                    case "&&" -> bl && br;
                    case "||" -> bl || br;
                    case "==" -> bl == br;
                    case "!=" -> bl != br;
                    default -> throw new RuntimeException("Unsupported operator for booleans: " + op);
                };

            } else if (left instanceof Double dl && right instanceof Double dr) {
                return switch (op) {
                    case "+" -> dl + dr;
                    case "-" -> dl - dr;
                    case "*" -> dl * dr;
                    case "/" -> {
                        if (dr == 0.0)
                            throw new ArithmeticException("Division by zero");
                        yield dl / dr;
                    }
                    case "**" -> {
                        if (dr < 0)
                            throw new ArithmeticException("Negative exponent not supported for doubles");
                        yield Math.pow(dl, dr);
                    }

                    case "%" -> {
                        if (dr == 0.0)
                            throw new ArithmeticException("Modulus by zero");
                        yield dl % dr;
                    }
                    case "==" -> dl == dr;
                    case "!=" -> dl != dr;
                    case ">" -> dl > dr;
                    case "<" -> dl < dr;
                    case ">=" -> dl >= dr;
                    case "<=" -> dl <= dr;
                    default -> throw new RuntimeException("Unsupported operator for doubles: " + op);
                };

            } else if (op.equals("+") && (left instanceof String || right instanceof String)) {
                return String.valueOf(left) + String.valueOf(right);
            } else {
                throw new RuntimeException("Unsupported operand types for operator '" + op + "': " +
                        (left != null ? left.getClass().getSimpleName() : "null") + " and " +
                        (right != null ? right.getClass().getSimpleName() : "null"));
            }
        } else if (expr instanceof UnaryExpression unary) {
            Object operand = evaluateExpression(unary.operand, currentClassName);
            String op = unary.operator;

            if (operand instanceof Integer i) {
                return switch (op) {
                    case "-" -> -i;
                    case "+" -> +i;
                    case "~" -> ~i;
                    default -> throw new RuntimeException("Unsupported unary operator for int: " + op);
                };
            } else if (operand instanceof Boolean b) {
                return switch (op) {
                    case "!" -> !b;
                    default -> throw new RuntimeException("Unsupported unary operator for boolean: " + op);
                };
            } else {
                throw new RuntimeException("Unsupported unary operand type: " + operand.getClass().getSimpleName());
            }
        } else if (expr instanceof IncrementExpression inc) {
            if (!locals.containsKey(inc.variableName())) {
                throw new RuntimeException("Undefined variable: " + inc.variableName());
            }
            Object current = locals.get(inc.variableName());
            if (!(current instanceof Integer)) {
                throw new RuntimeException("Can only increment/decrement integers");
            }
            int value = (int) current;
            int newVal = inc.operator().equals("++") ? value + 1 : value - 1;
            locals.put(inc.variableName(), newVal);
            return inc.isPrefix() ? newVal : value;
        } else if (expr instanceof AssignmentExpression assign) {
            String varName = assign.name();
            Object value = evaluateExpression(assign.right(), currentClassName);

            if (!locals.containsKey(varName) && !assign.isCompound()) {
                throw new RuntimeException("Variable '" + varName + "' is not declared");
            }

            if (assign.isCompound()) {
                Object current = locals.get(varName);
                value = applyOperator(assign.operator(), current, value);
            }

            locals.put(varName, value);
            return value;
        } else if (expr instanceof CompoundAssignmentExpression compoundAssign) {
            String varName = compoundAssign.variableName;
            String op = compoundAssign.operator.replace("=", ""); // "+=" → "+"
            Object right = evaluateExpression(compoundAssign.value, currentClassName);

            if (!locals.containsKey(varName)) {
                throw new RuntimeException("Variable '" + varName + "' is not declared");
            }

            Object left = locals.get(varName);
            Object result = applyOperator(op, left, right);
            locals.put(varName, result);
            return result;
        } else if (expr instanceof FunctionCall funcCall) {
            List<Object> args = funcCall.arguments().stream()
                    .map(arg -> evaluateExpression(arg, currentClassName))
                    .toList();
            return callMethod(currentClassName, funcCall.functionName(), args);
        }

        throw new RuntimeException("Unsupported expression: " + expr.getClass().getSimpleName());
    }

    private Object applyOperator(String operator, Object left, Object right) {
        if (left instanceof Integer l && right instanceof Integer r) {
            return switch (operator) {
                case "+" -> l + r;
                case "-" -> l - r;
                case "*" -> l * r;
                case "/" -> l / r;
                case "%" -> l % r;
                default -> throw new RuntimeException("Unsupported operator: " + operator);
            };
        }

        if (left instanceof String l && operator.equals("+")) {
            return l + right;
        }

        throw new RuntimeException("Invalid operands for operator '" + operator + "': " + left + ", " + right);
    }
}
