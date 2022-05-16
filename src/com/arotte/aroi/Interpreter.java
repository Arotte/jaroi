package com.arotte.aroi;

public class Interpreter implements Expr.Visitor<Object> {

    @Override
    public Object visitLiteralExpr(Expr.Literal expr) {
        return expr.value;
    }

    @Override
    public Object visitGroupingExpr(Expr.Grouping expr) {
        return evaluate(expr.expression);
    }

    @Override
    public Object visitUnaryExpr(Expr.Unary expr) {
        Object right = evaluate(expr.right);

        switch (expr.operator.type) {
            case BANG: return !isTruthy(right);
            case MINUS: return -(double)right;
        }

        // unreachable
        return null;
    }

    @Override
    public Object visitBinaryExpr(Expr.Binary expr) {
        Object left = evaluate(expr.left);
        Object right = evaluate(expr.right);

        switch (expr.operator.type) {
            case MINUS:   return (double)left - (double)right;
            case SLASH:   return (double)left / (double)right;
            case STAR:    return (double)left * (double)right;
            case GREATER: return (double)left > (double)right;
            case LESS:    return (double)left < (double)right;
            case GREATER_EQUAL: return (double)left >= (double)right;
            case LESS_EQUAL:    return (double)left <= (double)right;
            case BANG_EQUAL:    return !isEqual(left, right);
            case EQUAL_EQUAL:   return isEqual(left, right);
            case PLUS:
                if (left instanceof Double && right instanceof Double)
                    return (double)left + (double)right;
                if (left instanceof String && right instanceof String)
                    return (String)left + (String)right;
                break;
        }

        // unreachable
        return null;
    }

    private Object evaluate(Expr expr) {
        return expr.accept(this);
    }

    private boolean isTruthy(Object object) {
        // "false" and "nil" are falsey, and everything else is truthy
        if (object == null) return false;
        if (object instanceof Boolean) return (boolean)object;
        return true;
    }

    private boolean isEqual(Object a, Object b) {
        if (a == null && b == null) return true;
        if (a == null) return false;

        // use Java's built-in Object.equals method
        return a.equals(b);
    }
}
