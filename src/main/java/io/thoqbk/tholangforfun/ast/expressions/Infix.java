package io.thoqbk.tholangforfun.ast.expressions;

import io.thoqbk.tholangforfun.Token;

public class Infix extends Expression {
    private Expression left;
    private Expression right;

    public Infix(Token token) {
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

    @Override
    public String toString() {
        return String.format("(%s %s %s)", left, getToken(), right);
    }
}
