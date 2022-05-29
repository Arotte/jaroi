package com.arotte.aroi;

import java.util.List;

public class Interpreter implements Expr.Visitor<Object>,
                                    Stmt.Visitor<Void> {
    private Environment environment = new Environment();

    void interpret(List<Stmt> statements) {
        try {
            for (Stmt statement : statements)
                execute(statement);
        } catch (RuntimeError e) {
            Aroi.runtimeError(e);
        }
    }

    // ====================================================
    // Expression visitors

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
            case MINUS:
                checkNumberOperand(expr.operator, right);
                return -(double)right;
        }

        // unreachable
        return null;
    }

    @Override
    public Object visitBinaryExpr(Expr.Binary expr) {
        Object left = evaluate(expr.left);
        Object right = evaluate(expr.right);

        switch (expr.operator.type) {
            case MINUS:
                checkNumberOperands(expr.operator, left, right);
                return (double)left - (double)right;
            case SLASH:
                if ((double)right == 0)
                    throw new RuntimeError(expr.operator, "Dividing by zero is not cool.");

                checkNumberOperands(expr.operator, left, right);
                return (double)left / (double)right;
            case STAR:
                // allow string multiplication
                // eg. "s" * 2 will be "ss"
                if (isLeftString(left, right))
                    return ((String)left).repeat((int)(double)right);
                if (isRightString(left, right))
                    return ((String)right).repeat((int)(double)left);

                // default case: both are numbers
                checkNumberOperands(expr.operator, left, right);
                return (double)left * (double)right;
            case GREATER:
                checkNumberOperands(expr.operator, left, right);
                return (double)left > (double)right;
            case LESS:
                checkNumberOperands(expr.operator, left, right);
                return (double)left < (double)right;
            case GREATER_EQUAL:
                checkNumberOperands(expr.operator, left, right);
                return (double)left >= (double)right;
            case LESS_EQUAL:
                checkNumberOperands(expr.operator, left, right);
                return (double)left <= (double)right;
            case BANG_EQUAL:
                checkNumberOperands(expr.operator, left, right);
                return !isEqual(left, right);
            case EQUAL_EQUAL:
                checkNumberOperands(expr.operator, left, right);
                return isEqual(left, right);
            case PLUS:
                if (left instanceof Double && right instanceof Double)
                    return (double)left + (double)right;

                if (left instanceof String && right instanceof String)
                    return (String)left + (String)right;

                // support addition like "string" + 4 -> "string4"
                if (isLeftString(left, right))
                    return (String)left + stringify(right);
                if (isRightString(left, right))
                    return stringify(left) + (String)right;

                throw new RuntimeError(expr.operator, "Operands must be either numbers or strings");
        }

        // unreachable
        return null;
    }

    @Override
    public Object visitVariableExpr(Expr.Variable expr) {
        return environment.get(expr.name);
    }

    @Override
    public Object visitAssignExpr(Expr.Assign expr) {
        Object value = evaluate(expr);
        environment.assign(expr.name, value);
        return value;
    }


    // ====================================================
    // Statement visitors

    @Override
    public Void visitExpressionStmt(Stmt.Expression stmt) {
        evaluate(stmt.expression);
        return null;
    }

    @Override
    public Void visitIfStmt(Stmt.If stmt) {
        if (isTruthy(evaluate(stmt.condition))) {
            execute(stmt.thenBranch);
        } else if (stmt.elseBranch != null) {
            execute(stmt.elseBranch);
        }
        return null;
    }

    @Override
    public Void visitPrintStmt(Stmt.Print stmt) {
        Object value = evaluate(stmt.expression);
        System.out.println(stringify(value));
        return null;
    }

    @Override
    public Void visitVarStmt(Stmt.Var stmt) {
        Object value = null;
        if (stmt.initializer != null) {
            value = evaluate(stmt.initializer);
        }

        // if the variable is not initialized,
        // its default value will be 'nil'
        environment.define(stmt.name.lexeme, value);
        return null;
    }

    @Override
    public Void visitBlockStmt(Stmt.Block stmt) {
        executeBlock(stmt.statements, new Environment(environment));
        return null;
    }

    // ====================================================

    private Object evaluate(Expr expr) {
        return expr.accept(this);
    }

    private void execute(Stmt stmt) {
        stmt.accept(this);
    }

    void executeBlock(List<Stmt> statements, Environment environment) {
        Environment previous = this.environment;
        try {
            // temporarily change the environment of
            // the interpreter to execute the list of statements
            // inside the block scope
            this.environment = environment;

            for (Stmt statement : statements)
                execute(statement);
        } finally {
            this.environment = previous;
        }
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

    private boolean isLeftString(Object left, Object right) {
        return left instanceof String && right instanceof Double;
    }

    private boolean isRightString(Object left, Object right) {
        return left instanceof Double && right instanceof String;
    }

    private void checkNumberOperand(Token operator, Object operand) {
        // operand must be a Double
        if (operand instanceof Double) return;
        throw new RuntimeError(operator, "Operand must be a number");
    }

    private void checkNumberOperands(Token operator, Object right, Object left) {
        // operands must be Doubles
        if (left instanceof Double && right instanceof Double) return;
        throw new RuntimeError(operator, "Operands must be numbers.");
    }

    private String stringify(Object o) {
        if (o == null) return "nil";

        if (o instanceof Double) {
            String txt = o.toString();
            if (txt.endsWith(".0")) // remove ".0"
                txt = txt.substring(0, txt.length() - 2);
            return txt;
        }

        return o.toString();
    }
}
