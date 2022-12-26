package io.thoqbk.tholangforfun.ast.expressions;

import io.thoqbk.tholangforfun.Token;

public class Identifier extends Expression {

    public Identifier(Token token) {
        super(token);
    }

    @Override
    public String toString() {
        return getToken().getLiteral();
    }
}
