package io.thoqbk.tholangforfun.ast.statements;

import io.thoqbk.tholangforfun.Token;
import io.thoqbk.tholangforfun.ast.expressions.Expression;

public class Put extends Statement{
    private Expression expression;

    public Put(Token token) {
        super(token);
    }

    public void setExpression(Expression expression) {
        this.expression = expression;
    }

    public Expression getExpression() {
        return expression;
    }
}
