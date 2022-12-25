package io.thoqbk.tholangforfun.ast;

import io.thoqbk.tholangforfun.Token;

public class PrefixExpression extends Expression {
    private Expression right;

    public PrefixExpression(Token token) {
        super(token);
    }

    public void setRight(Expression right) {
        this.right = right;
    }

    public Expression getRight() {
        return right;
    }
}
