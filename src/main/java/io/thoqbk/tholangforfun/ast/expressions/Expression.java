package io.thoqbk.tholangforfun.ast.expressions;

import io.thoqbk.tholangforfun.Token;

public class Expression {
    // start token of the expression e.g. ! in prefix expression
    private Token token;

    public Expression(Token token) {
        this.token = token;
    }

    public Token getToken() {
        return this.token;
    }

    public <T extends Expression> T as(Class<T> clazz) {
        if (!clazz.isInstance(this)) {
            throw new RuntimeException(
                    "Expected " + clazz.getName() + ", received " + this.getClass().getName() + " instead");
        }
        return clazz.cast(this);
    }
}
