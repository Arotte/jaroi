package com.arotte.aroi;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**Parser.java
 *
 * This class parses the list of scanned tokens,
 * and constructs the Abstract Syntax Tree structure.
 *
 * Uses Recursive Descent Parsing.
 */
public class Parser {
    private static class ParseError extends RuntimeException {}

    private final List<Token> tokens;
    private int current = 0;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    List<Stmt> parse() {
        List<Stmt> statements = new ArrayList<>();
        while (!isAtEnd()) statements.add(declaration());

        return statements;
    }

    // =====================================================
    // rules

    private Stmt declaration() {
        try {
            if (match(TokenType.VAR)) return varDeclaration();

            return statement();
        } catch (ParseError error) {
            synchronize();
            return null;
        }
    }

    private Stmt varDeclaration() {
        return null;
    }

    private Stmt statement() {
        if (match(TokenType.PRINT)) return printStatement();

        return expressionStatement();
    }

    private Stmt printStatement() {
        Expr value = expression();
        consume(TokenType.SEMICOLON, "Expected ';' after value.");
        return new Stmt.Print(value);
    }

    private Stmt expressionStatement() {
        Expr expr = expression();
        consume(TokenType.SEMICOLON, "Expected ';' after expression.");
        return new Stmt.Expression(expr);
    }

    private Expr expression() {
        return equality();
    }

    private Expr equality() {
        Expr expr = comparison();
        while (match(TokenType.EQUAL_EQUAL, TokenType.BANG_EQUAL)) {
            Token operator = previous();
            Expr right = comparison();
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }

    private Expr comparison() {
        Expr expr = term();
        while (match(TokenType.GREATER, TokenType.GREATER_EQUAL,
                     TokenType.LESS,    TokenType.LESS_EQUAL)) {
            Token operator = previous();
            Expr right = term();
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }

    private Expr term() {
        Expr expr = factor();
        while (match(TokenType.MINUS, TokenType.PLUS)) {
            Token operator = previous();
            Expr right = factor();
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }

    private Expr factor() {
        Expr expr = unary();
        while (match(TokenType.SLASH, TokenType.STAR)) {
            Token operator = previous();
            Expr right = unary();
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }

    private Expr unary() {
        if (match(TokenType.BANG, TokenType.MINUS)) {
            Token operator = previous();
            Expr right = unary();
            return new Expr.Unary(operator, right);
        }
        return primary();
    }

    private Expr primary() {
        if (match(TokenType.TRUE)) return new Expr.Literal(true);
        if (match(TokenType.FALSE)) return new Expr.Literal(false);
        if (match(TokenType.NIL)) return new Expr.Literal(null);

        // number or string literal
        if (match(TokenType.NUMBER, TokenType.STRING)) {
            return new Expr.Literal(previous().literal);
        }

        // grouping
        if (match(TokenType.LEFT_PAREN)) {
            // consume the expression inside "( )"
            Expr expr = expression();
            // consume the right ")"
            consume(TokenType.RIGHT_PAREN, "Expected closing ')' after expression.");
            return new Expr.Grouping(expr);
        }

        throw error(peek(), "No expected expression found.");
    }
    
    // =====================================================
    // helpers

    private boolean match(TokenType... types) {
        // check if the current token has any of the given types
        // if so, consume token, otherwise leave it alon
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }

        return false;
    }

    private Token consume(TokenType type, String errorMessage) {
        if (check(type)) return advance();

        throw error(peek(), errorMessage);
    }
    
    private boolean check(TokenType type) {
        // returns true if the current token is of given type
        // never consumes the token
        if (isAtEnd()) return false;
        return peek().type == type;
    }

    private Token advance() {
        // consume and return current token
        if (!isAtEnd()) current++;
        return previous();
    }

    private boolean isAtEnd() {
        return peek().type == TokenType.EOF;
    }

    private Token peek() {
        return tokens.get(current);
    }

    private Token previous() {
        return tokens.get(current - 1);
    }

    private ParseError error(Token token, String errorMessage) {
        Aroi.error(token, errorMessage);
        return new ParseError();
    }

    private void synchronize() {
        advance();

        while (!isAtEnd()) {
            if (previous().type == TokenType.SEMICOLON) return;

            switch (peek().type) {
                case CLASS, FUN, VAR, FOR, IF, WHILE, PRINT, RETURN -> {
                    return;
                }
            }

            advance();
        }
    }
}
