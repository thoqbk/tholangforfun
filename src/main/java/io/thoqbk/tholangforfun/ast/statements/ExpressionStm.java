package io.thoqbk.tholangforfun.ast.statements;

import io.thoqbk.tholangforfun.ast.expressions.Expression;

public class ExpressionStm extends Statement {
    private final Expression expression;

    public ExpressionStm(Expression expression) {
        super(null);
        this.expression = expression;
    }

    public Expression getExpression() {
        return expression;
    }
}
