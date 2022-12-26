package io.thoqbk.tholangforfun.ast.expressions;

import io.thoqbk.tholangforfun.Token;

public class Prefix extends Expression {
    private Expression right;

    public Prefix(Token token) {
        super(token);
    }

    public void setRight(Expression right) {
        this.right = right;
    }

    public Expression getRight() {
        return right;
    }

    @Override
    public String toString() {
        return String.format("(%s%s)", getToken().getLiteral(), right);
    }
}
