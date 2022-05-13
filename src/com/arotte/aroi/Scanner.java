package com.arotte.aroi;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
class Scanner {
    private final String source;
    private final List<Token> tokens = new ArrayList<>();

    // bookkeeping
    // first character of a lexeme
    private int start = 0;
    // character currently being considered
    private int current = 0;
    // line of current
    private int line = 1;

    Scanner(String source) {
        this.source = source;
    }

    List<Token> scanTokens() {
        while(!isAtEnd()) {
            start = current;
            scanToken();
        }

        // reached end of string, add EOF token
        tokens.add(new Token(TokenType.EOF, "", null, line));
        return tokens;
    }

    private void scanToken() {
        char c = advance();
        switch (c) {
            // single-character tokens
            case ('(') -> addToken(TokenType.LEFT_PAREN);
            case (')') -> addToken(TokenType.RIGHT_PAREN);
            case ('{') -> addToken(TokenType.LEFT_BRACE);
            case ('}') -> addToken(TokenType.RIGHT_BRACE);
            case (',') -> addToken(TokenType.COMMA);
            case ('.') -> addToken(TokenType.DOT);
            case ('+') -> addToken(TokenType.PLUS);
            case (';') -> addToken(TokenType.SEMICOLON);
            case ('*') -> addToken(TokenType.STAR);

            // single or two character tokens
            case ('!') -> addToken(
                    match('=') ? TokenType.BANG_EQUAL : TokenType.BANG);
            case ('=') -> addToken(
                    match('=') ? TokenType.EQUAL_EQUAL : TokenType.EQUAL);
            case ('>') -> addToken(
                    match('=') ? TokenType.GREATER_EQUAL : TokenType.GREATER);
            case ('<') -> addToken(
                    match('=') ? TokenType.LESS_EQUAL : TokenType.LESS);

            // slash
            case ('/') -> {
                // a comment goes until the end of the line
                if (match('/'))
                    while(peek() != '\n' && !isAtEnd()) advance();
                else
                    addToken(TokenType.SLASH);
            }

            // newlines and whitespaces
            case ' ', '\r', '\t' -> { }
            case ('\n') -> line++;

            default -> Aroi.error(line, "Unexpected character.");
        }
    }

    private boolean match(char expected) {
        if (isAtEnd()) return false;
        if (source.charAt(current) != expected) return false;

        // if match found, consume character
        current++;
        return true;
    }

    private char peek() {
        // one character lookahead, does not consume chars
        if (isAtEnd()) return '\0';
        return source.charAt(current);
    }

    private boolean isAtEnd() {
        return current >= source.length();
    }

    private char advance() {
        return source.charAt(current++);
    }

    private void addToken(TokenType type) {
        addToken(type, null);
    }

    private void addToken(TokenType type, Object literal) {
        String text = source.substring(start, current);
        tokens.add(
                new Token(type, text, literal, line)
        );
    }
}
