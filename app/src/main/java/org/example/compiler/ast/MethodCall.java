package org.example.compiler.ast;
import java.util.List;

public class MethodCall implements ExpressionNode {
    public final ExpressionNode target;
    public final String methodName;
    public final List<ExpressionNode> arguments;

    public MethodCall(ExpressionNode target, String methodName, List<ExpressionNode> arguments) {
        this.target = target;
        this.methodName = methodName;
        this.arguments = arguments;
    }
}
