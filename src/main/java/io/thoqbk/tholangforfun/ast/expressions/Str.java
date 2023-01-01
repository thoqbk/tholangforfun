package io.thoqbk.tholangforfun.ast.expressions;

import io.thoqbk.tholangforfun.Token;

public class Str extends Expression {
    private String value;

    public Str(Token token) {
        super(token);
        this.value = token.getLiteral();
    }

    public String getValue() {
        return value;
    }
}
