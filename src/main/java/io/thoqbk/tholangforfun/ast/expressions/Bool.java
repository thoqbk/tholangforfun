package io.thoqbk.tholangforfun.ast.expressions;

import io.thoqbk.tholangforfun.Token;

public class Bool extends Expression {
    private boolean value;

    public Bool(Token token) {
        super(token);
        value = "true".equals(token.getLiteral());
    }

    public boolean getValue() {
        return value;
    }
}
