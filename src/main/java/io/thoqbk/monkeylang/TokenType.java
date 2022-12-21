package io.thoqbk.monkeylang;

import java.util.Map;

public enum TokenType {
    ILLEGAL("ILLEGAL"),
    EOF("EOF"),
    IDENT("IDENT"), // add, foobar
    INT("INT"), // 123

    // Operators
    ASSIGN("="),
    PLUS("+"),
    MINUS("-"),
    BANG("!"),
    ASTERISK("*"),
    SLASH("/"),

    LT("<"),
    GT(">"),

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
    RETURN("RETURN");

    private static final Map<String, TokenType> KEYWORDS = Map.of(
            "fn", FUNCTION,
            "let", LET,
            "true", TRUE,
            "false", FALSE,
            "if", IF,
            "else", ELSE,
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
