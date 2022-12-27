package io.thoqbk.tholangforfun.ast.expressions;

import io.thoqbk.tholangforfun.Token;
import io.thoqbk.tholangforfun.TokenType;

public class Bool extends Expression {
    private boolean value;

    public Bool(Token token) {
        super(token);
        value = token.getType() == TokenType.TRUE;
    }

    public boolean getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "" + value;
    }
}
