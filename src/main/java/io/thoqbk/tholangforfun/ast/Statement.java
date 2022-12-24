package io.thoqbk.tholangforfun.ast;

import io.thoqbk.tholangforfun.Token;

public class Statement {
    protected Token token;

    public Statement(Token token) {
        this.token = token;
    }

    public Token getToken() {
        return this.token;
    }

    public <T extends Statement> T as(Class<T> clazz) {
        if (!clazz.isInstance(this)) {
            throw new RuntimeException("Invalid class. Expected " + clazz.getName() + " was " + this.getClass().getName());
        }
        return clazz.cast(this);
    }
}
