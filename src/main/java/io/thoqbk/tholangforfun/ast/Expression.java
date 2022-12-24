package io.thoqbk.tholangforfun.ast;

import io.thoqbk.tholangforfun.Token;

public class Expression {
    private Token token;
    
    public Expression(Token token) {
        this.token = token;
    }

    public Token getToken() {
        return this.token;
    }
}
