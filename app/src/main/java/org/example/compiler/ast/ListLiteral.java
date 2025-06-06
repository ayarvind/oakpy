package org.example.compiler.ast;
import java.util.List;
public class ListLiteral implements ExpressionNode {
    public final List<ExpressionNode> elements;

    public ListLiteral(List<ExpressionNode> elements) {
        this.elements = elements;
    }

}
