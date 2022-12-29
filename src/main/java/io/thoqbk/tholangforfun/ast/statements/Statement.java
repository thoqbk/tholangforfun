package io.thoqbk.tholangforfun.ast.statements;

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
        return clazz.cast(this);
    }

    public <T extends Statement> boolean is(Class<T> clazz) {
        return clazz.isInstance(this);
    }
}
