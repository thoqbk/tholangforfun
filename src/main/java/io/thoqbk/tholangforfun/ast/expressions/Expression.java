package io.thoqbk.tholangforfun.ast.expressions;

import io.thoqbk.tholangforfun.Token;

public abstract class Expression {
    // start token of the expression e.g. ! in prefix expression
    private Token token;

    public Expression(Token token) {
        this.token = token;
    }

    public Token getToken() {
        return this.token;
    }

    public <T extends Expression> T as(Class<T> clazz) {
        return clazz.cast(this);
    }
}
