package io.thoqbk.tholangforfun.ast;

import io.thoqbk.tholangforfun.ast.expressions.Expression;

public class ExpressionStatement extends Statement {
    private final Expression expression;

    public ExpressionStatement(Expression expression) {
        super(null);
        this.expression = expression;
    }

    public Expression getExpression() {
        return expression;
    }
}
