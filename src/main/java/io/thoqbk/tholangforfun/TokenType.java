package io.thoqbk.tholangforfun;

import java.util.Map;

public enum TokenType {
    ILLEGAL("ILLEGAL"),
    EOF("EOF"),
    IDENT("IDENT"), // add, foobar
    INT("INT"), // 123
    STRING("STRING"), // "foo bar"

    // Operators
    ASSIGN("="),
    PLUS("+"),
    MINUS("-"),
    BANG("!"),
    ASTERISK("*"),
    SLASH("/"),

    LT("<"),
    GT(">"),
    
    LTE("<="),
    GTE(">="),

    EQ("=="),
    NOT_EQ("!="),

    // Delimiters
    COMMA(","),
    SEMICOLON(";"),

    LPAREN("("),
    RPAREN(")"),
    LBRACE("{"),
    RBRACE("}"),

    // Keywords
    FUNCTION("FUNCTION"),
    LET("LET"),
    TRUE("TRUE"),
    FALSE("FALSE"),
    IF("IF"),
    ELSE("ELSE"),
    PUT("PUT"),
    WHILE("while"),
    RETURN("RETURN");

    private static final Map<String, TokenType> KEYWORDS = Map.of(
            "function", FUNCTION,
            "let", LET,
            "true", TRUE,
            "false", FALSE,
            "if", IF,
            "else", ELSE,
            "put", PUT,
            "while", WHILE,
            "return", RETURN);

    private final String value;

    private TokenType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }

    public static TokenType lookupIdent(String value) {
        return KEYWORDS.getOrDefault(value, IDENT);
    }
}
