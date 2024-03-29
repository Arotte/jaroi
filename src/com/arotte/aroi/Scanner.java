package com.arotte.aroi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
class Scanner {
    private final String source;
    private final List<Token> tokens = new ArrayList<>();

    // reserved keywords
    private static final Map<String, TokenType> keywords;
    static {
        keywords = new HashMap<>();
        keywords.put("and", TokenType.AND);
        keywords.put("class", TokenType.CLASS);
        keywords.put("if", TokenType.IF);
        keywords.put("else", TokenType.ELSE);
        keywords.put("true", TokenType.TRUE);
        keywords.put("false", TokenType.FALSE);
        keywords.put("fun", TokenType.FUN);
        keywords.put("for", TokenType.FOR);
        keywords.put("nil", TokenType.NIL);
        keywords.put("or", TokenType.OR);
        keywords.put("scream", TokenType.PRINT);
        keywords.put("return", TokenType.RETURN);
        keywords.put("super", TokenType.SUPER);
        keywords.put("this", TokenType.THIS);
        keywords.put("var", TokenType.VAR);
        keywords.put("while", TokenType.WHILE);
    }

    // first character of a lexeme
    private int start = 0;
    // character currently being considered
    private int current = 0;
    // line of current character
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
            case ('-') -> addToken(TokenType.MINUS);
            case ('+') -> addToken(TokenType.PLUS);
            case (';') -> addToken(TokenType.SEMICOLON);
            case ('*') -> addToken(TokenType.STAR);

            // single or two character tokens
            case ('!') -> addToken(match('=') ? TokenType.BANG_EQUAL : TokenType.BANG);
            case ('=') -> addToken(match('=') ? TokenType.EQUAL_EQUAL : TokenType.EQUAL);
            case ('>') -> addToken(match('=') ? TokenType.GREATER_EQUAL : TokenType.GREATER);
            case ('<') -> addToken(match('=') ? TokenType.LESS_EQUAL : TokenType.LESS);

            // slash
            case ('/') -> {
                // regular comment with "//"
                if (match('/')) {
                    // a comment goes until the end of the line
                    while (peek() != '\n' && !isAtEnd()) advance();

                // multiline comment with "/* */"
                } else if (match('*')) {
                    while (!match('*') && peekNext() != '/' && !isAtEnd()) {
                        advance();
                        if (match('\n')) line++;
                    }
                    // consume the last "/"
                    advance();

                } else {
                    addToken(TokenType.SLASH);
                }
            }

            // strings
            case ('"') -> string();

            // newlines and whitespaces
            case ' ', '\r', '\t' -> { }
            case ('\n') -> line++;

            default -> {
                // number literals
                if (isDigit(c))
                    number();
                // identifiers
                else if (isAlpha(c))
                    identifier();
                else
                    Aroi.error(line, "Unexpected character.");
            }
        }
    }

    private void identifier() {
        // consume the identifier
        while(isAlphanumeric(peek())) advance();

        // check if the identifier is reserved keyword
        String text = source.substring(start, current);
        TokenType type = keywords.get(text);

        // if not, it is a regular, user-defined identifier
        if (type == null) type = TokenType.IDENTIFIER;

        addToken(type);
    }

    private void number() {
        // consume number until "."
        while(isDigit(peek())) advance();

        // look for a fractional part
        if (peek() == '.' && isDigit(peekNext())) {
            // consume the "."
            advance();

            // consume the number after the "."
            while (isDigit(peek())) advance();
        }

        // add number token
        addToken(TokenType.NUMBER,
                Double.parseDouble(
                        source.substring(start, current)
                ));
    }

    private void string() {
        // consume the string
        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '"') line++;
            advance();
        }

        if (isAtEnd()) {
            Aroi.error(line, "Unterminated string.");
            return;
        }

        // consume the closing "
        advance();

        // trim the surrounding "s
        String value = source.substring(start + 1, current - 1);
        addToken(TokenType.STRING, value);
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

    private char peekNext() {
        // two-character lookahead, does not consume chars
        if (current + 1 >= source.length()) return '\0';
        return source.charAt(current + 1);
    }

    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') ||
               (c >= 'A' && c <= 'Z') ||
                c == '_';
    }

    private boolean isAlphanumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
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
