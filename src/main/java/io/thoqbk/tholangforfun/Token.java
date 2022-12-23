package io.thoqbk.tholangforfun;

public class Token {
    private TokenType type;
    private String literal;

    public Token(TokenType type, String literal) {
        this.type = type;
        this.literal = literal;
    }

    public Token(TokenType type, int literal) {
        this(type, Character.toString(literal));
    }

    public TokenType getType() {
        return type;
    }

    public String getLiteral() {
        return literal;
    }

    @Override
    public String toString() {
        return "Type " + type + ", literal: " + literal;
    }
}
