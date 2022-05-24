package com.arotte.aroi;

/**AstPrinter.java
 *
 * Prints an AST in a (semi-)human-readable format.
 * Implements the Visitor interface.
 */
public class AstPrinter implements Expr.Visitor<String> {
    String print(Expr expr) {
        return expr.accept(this);
    }

    @Override
    public String visitLiteralExpr(Expr.Literal expr) {
        if (expr.value == null) return "nil";
        return expr.value.toString();
    }

    @Override
    public String visitGroupingExpr(Expr.Grouping expr) {
        return parenthesize(
                "group",
                expr.expression
        );
    }

    @Override
    public String visitUnaryExpr(Expr.Unary expr) {
        return parenthesize(
                expr.operator.lexeme,
                expr.right
        );
    }

    @Override
    public String visitBinaryExpr(Expr.Binary expr) {
        return parenthesize(
                expr.operator.lexeme,
                expr.left,
                expr.right
        );
    }

    @Override
    public String visitVariableExpr(Expr.Variable expr) {
        return expr.name.lexeme;
    }

    private String parenthesize(String name, Expr... exprs) {
        // take a name and a list of subexpressions, and wrap them
        // in parentheses, eg: (- 23 11)
        StringBuilder builder = new StringBuilder();

        builder.append("(").append(name);
        for (Expr expr : exprs) {
            builder.append(" ");
            // recursive step: call accept() on each subexpression
            builder.append(expr.accept(this));
        }
        builder.append(")");

        return builder.toString();
    }
}
