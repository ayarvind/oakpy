package org.example.compiler.interpreter;

import org.example.compiler.ast.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Interpreter {

    // Map class name to ClassDeclaration AST node
    private final Map<String, ClassDeclaration> classes = new HashMap<>();

    // Current function's local variable environment (simple)
    private Map<String, Object> locals;

    /**
     * Executes the main program body. Registers all classes and then runs the main method.
     * @param programBody A list of statement nodes representing the program's top-level structure.
     */
    public void executeProgram(List<StatementNode> programBody) {
        // Register all classes by mapping their names to their AST nodes
        for (StatementNode stmt : programBody) {
            if (stmt instanceof ClassDeclaration cls) {
                classes.put(cls.name(), cls);
            }
        }
        // After registering classes, run the main method
        runMain();
    }

    /**
     * Locates and executes the 'main' method within the registered classes.
     * Throws a RuntimeException if no main method is found.
     */
    private void runMain() {
        for (ClassDeclaration cls : classes.values()) {
            for (StatementNode member : cls.body()) {
                if (member instanceof FunctionDeclaration func && func.name().equals("main")) {
                    System.out.println("Running main in class " + cls.name());
                    // Call the main method, passing its class name dynamically
                    callMethod(cls.name(), "main");
                    return;
                }
            }
        }
        throw new RuntimeException("No main method found in any class");
    }

    /**
     * Calls a specific method within a given class.
     * @param className The name of the class containing the method.
     * @param methodName The name of the method to call.
     * @throws RuntimeException if the class or method is not found.
     */
    public void callMethod(String className, String methodName) {
        ClassDeclaration cls = classes.get(className);
        if (cls == null) {
            throw new RuntimeException("Class not found: " + className);
        }

        // Find the function (method) in the class body
        for (StatementNode member : cls.body()) {
            if (member instanceof FunctionDeclaration func && func.name().equals(methodName)) {
                // Pass the class name to executeFunction so it knows its context
                executeFunction(func, className);
                return;
            }
        }
        throw new RuntimeException("Method " + methodName + " not found in class " + className);
    }

    /**
     * Executes the body of a function (method).
     * Creates a new local scope for variables within this function.
     * @param func The FunctionDeclaration AST node to execute.
     * @param currentClassName The name of the class this function belongs to.
     */
    private void executeFunction(FunctionDeclaration func, String currentClassName) {
        // Create a fresh local scope for variables for this function call
        // In a real interpreter, you'd manage a stack of scopes for proper function calls.
        locals = new HashMap<>();

        for (StatementNode stmt : func.body()) {
            // Pass the current class name to executeStatement
            executeStatement(stmt, currentClassName);
        }
    }

    /**
     * Executes a single statement.
     * @param stmt The StatementNode AST node to execute.
     * @param currentClassName The name of the class where this statement is being executed.
     * @throws RuntimeException if the statement type is unsupported.
     */
    private void executeStatement(StatementNode stmt, String currentClassName) {
        if (stmt instanceof VarDeclaration varDecl) {
            // Evaluate the initial value and store it in local variables
            Object value = evaluateExpression(varDecl.value());
            locals.put(varDecl.name(), value);

        } else if (stmt instanceof PrintStatement printStmt) {
            // Evaluate the expression and print its value to console
            Object val = evaluateExpression(printStmt.expression());
            System.out.println(val);

        } else if (stmt instanceof ExpressionStatement exprStmt) {
            // This handles expressions that are statements, like function calls
            if (exprStmt.expression() instanceof FunctionCall funcCall) {
                // Dynamically call the method using the current class name
                callMethod(currentClassName, funcCall.functionName());
            } else {
                // If it's just an expression like '5 + 3;', evaluate it but discard the result
                evaluateExpression(exprStmt.expression());
            }

        } else {
            throw new RuntimeException("Unsupported statement: " + stmt.getClass().getSimpleName());
        }
    }

    /**
     * Evaluates an expression node and returns its computed value.
     * This is the core logic for handling different types of expressions.
     * @param expr The ExpressionNode AST node to evaluate.
     * @return The result of the expression evaluation as an Object.
     * @throws RuntimeException if the expression type is unsupported or if an operation is invalid.
     */
    private Object evaluateExpression(ExpressionNode expr) {
        if (expr instanceof NumberLiteral number) {
            // Return the raw value for number literals (assuming Integer for now)
            return number.value;

        } else if (expr instanceof StringLiteral string) {
            // Return the raw string value
            return string.value;

        } else if (expr instanceof Identifier ident) {
            // Look up the variable's value in the local scope
            if (locals.containsKey(ident.name())) {
                return locals.get(ident.name());
            } else {
                throw new RuntimeException("Undefined variable: " + ident.name());
            }

        } else if (expr instanceof BinaryExpression binExpr) {
            // Recursively evaluate left and right operands
            Object left = evaluateExpression(binExpr.left);
            Object right = evaluateExpression(binExpr.right);
            String op = binExpr.operator;

            // --- Handle operations on Integer operands ---
            if (left instanceof Integer l && right instanceof Integer r) {
                switch (op) {
                    // Arithmetic Operators
                    case "+":
                        return l + r;
                    case "-":
                        return l - r;
                    case "*":
                        return l * r;
                    case "/":
                        if (r == 0) {
                            throw new ArithmeticException("Division by zero");
                        }
                        return l / r;
                    case "%":
                        if (r == 0) {
                            throw new ArithmeticException("Modulus by zero");
                        }
                        return l % r;

                    // Bitwise Operators
                    case "&": // Bitwise AND
                        return l & r;
                    case "|": // Bitwise OR
                        return l | r;
                    case "^": // Bitwise XOR
                        return l ^ r;

                    // Shift Operators
                    case "<<": // Left Shift
                        return l << r;
                    case ">>": // Signed Right Shift
                        return l >> r;
                    case ">>>": // Unsigned Right Shift (Zero-fill right shift)
                        return l >>> r;

                    // Relational Operators (return boolean)
                    case "==":
                        return l == r;
                    case "!=":
                        return l != r;
                    case ">":
                        return l > r;
                    case "<":
                        return l < r;
                    case ">=":
                        return l >= r;
                    case "<=":
                        return l <= r;

                    default:
                        throw new RuntimeException("Unsupported operator for integers: " + op);
                }
            }
            // --- Handle operations on Boolean operands ---
            else if (left instanceof Boolean bl && right instanceof Boolean br) {
                switch (op) {
                    // Logical Operators
                    case "&&": // Logical AND
                        return bl && br;
                    case "||": // Logical OR
                        return bl || br;

                    // Equality for Booleans
                    case "==":
                        return bl == br;
                    case "!=":
                        return bl != br;

                    default:
                        throw new RuntimeException("Unsupported operator for booleans: " + op);
                }
            }
            // --- Handle operations on Double operands (Example - extend for float/long if needed) ---
            else if (left instanceof Double dl && right instanceof Double dr) {
                switch (op) {
                    // Arithmetic Operators
                    case "+": return dl + dr;
                    case "-": return dl - dr;
                    case "*": return dl * dr;
                    case "/":
                        if (dr == 0.0) {
                            throw new ArithmeticException("Division by zero (double)");
                        }
                        return dl / dr;
                    case "%": // Modulus for doubles
                        if (dr == 0.0) {
                            throw new ArithmeticException("Modulus by zero (double)");
                        }
                        return dl % dr;

                    // Relational Operators (return boolean)
                    case "==": return dl == dr;
                    case "!=": return dl != dr;
                    case ">": return dl > dr;
                    case "<": return dl < dr;
                    case ">=": return dl >= dr;
                    case "<=": return dl <= dr;

                    default:
                        throw new RuntimeException("Unsupported operator for doubles: " + op);
                }
            }
            // --- Handle String concatenation ---
            else if (op.equals("+") && (left instanceof String || right instanceof String)) {
                // If either operand is a String, perform string concatenation
                return String.valueOf(left) + String.valueOf(right);
            }
            // --- No compatible types found for the given operator ---
            else {
                throw new RuntimeException("Unsupported operand types for operator '" + op + "': " +
                        (left != null ? left.getClass().getSimpleName() : "null") + " and " +
                        (right != null ? right.getClass().getSimpleName() : "null"));
            }
        }
        // --- Handle other expression types (e.g., UnaryExpression, FunctionCallExpression if they return a value) ---
        // If an expression type is not handled, it's an unsupported expression
        throw new RuntimeException("Unsupported expression: " + expr.getClass().getSimpleName());
    }
}
