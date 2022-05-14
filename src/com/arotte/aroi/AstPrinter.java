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
        return null;
    }

    @Override
    public String visitGroupingExpr(Expr.Grouping expr) {
        return null;
    }

    @Override
    public String visitUnaryExpr(Expr.Unary expr) {
        return null;
    }

    @Override
    public String visitBinaryExpr(Expr.Binary expr) {
        return null;
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
