package io.thoqbk.tholangforfun.ast;

import io.thoqbk.tholangforfun.Token;

public class ReturnStatement extends Statement {
    private Expression expression;

    public ReturnStatement(Token token) {
        super(token);
    }

    public Expression getExpression() {
        return expression;
    }

    public void setExpression(Expression expression) {
        this.expression = expression;
    }
}
