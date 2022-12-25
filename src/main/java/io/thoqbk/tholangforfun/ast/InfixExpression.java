package io.thoqbk.tholangforfun.ast;

import io.thoqbk.tholangforfun.Token;

public class InfixExpression extends Expression {
    private Expression left;
    private Expression right;

    public InfixExpression(Token token) {
        super(token);
    }

    public void setLeft(Expression left) {
        this.left = left;
    }

    public Expression getLeft() {
        return left;
    }

    public void setRight(Expression right) {
        this.right = right;
    }

    public Expression getRight() {
        return right;
    }
}
