package io.thoqbk.tholangforfun.ast.expressions;

import io.thoqbk.tholangforfun.Token;

public class Int extends Expression {
    private int value;

    public Int(Token token) {
        super(token);
        this.value = Integer.parseInt(token.getLiteral());
    }

    public int getValue() {
        return value;
    }
}
