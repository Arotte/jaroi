package com.arotte.aroi;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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

    Expr parse() {
        try {
            return expression();
        } catch (ParseError e) {
            return null;
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            // TODO: report
            return null;
        }
    }

    // =====================================================
    // rules

    private Expr expression()
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException
    {
        return equality();
    }

    private Expr equality()
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException
    {
        return parseLeftBinary(
                Parser.class.getDeclaredMethod("term"),
                TokenType.EQUAL_EQUAL, TokenType.BANG_EQUAL);
    }

    private Expr comparison()
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException
    {
        return parseLeftBinary(
                Parser.class.getDeclaredMethod("comparison"),
                TokenType.GREATER,
                TokenType.GREATER_EQUAL,
                TokenType.LESS,
                TokenType.LESS_EQUAL
        );
    }

    private Expr term()
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException
    {
        return parseLeftBinary(
                Parser.class.getDeclaredMethod("factor"),
                TokenType.MINUS,
                TokenType.PLUS
        );
    }

    private Expr factor()
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException
    {
        return parseLeftBinary(
                Parser.class.getDeclaredMethod("unary"),
                TokenType.SLASH,
                TokenType.STAR
        );
    }

    private Expr unary()
            throws InvocationTargetException, NoSuchMethodException, IllegalAccessException
    {
        if (match(TokenType.BANG, TokenType.MINUS)) {
            Token operator = previous();
            Expr right = unary();
            return new Expr.Unary(operator, right);
        }

        return primary();
    }

    private Expr primary()
            throws InvocationTargetException, NoSuchMethodException, IllegalAccessException
    {
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
    // general/common parser helpers

    private Expr parseLeftBinary( Method ruleMethod, TokenType... types)
            throws InvocationTargetException, IllegalAccessException {
        // helper method for parsing left-associative binary operators

        // left-hand side of the expression
        Expr expr = (Expr) ruleMethod.invoke(Parser.class);

        // right-hand side of the expression
        // if no operator found, we have reached the end of the expression
        while (match(types)) {
            // add a new binary expression
            Token operator = previous();
            Expr right = (Expr) ruleMethod.invoke(Parser.class);
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
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
