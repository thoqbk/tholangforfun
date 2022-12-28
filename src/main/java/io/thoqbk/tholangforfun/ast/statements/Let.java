package io.thoqbk.tholangforfun.ast.statements;

import io.thoqbk.tholangforfun.Token;
import io.thoqbk.tholangforfun.ast.expressions.Expression;

public class Let extends Statement {
    private String variableName;
    private Expression expression;

    public Let(Token token) {
        super(token);
    }

    public String getVariableName() {
        return variableName;
    }

    public void setVariableName(String name) {
        this.variableName = name;
    }

    public Expression getExpression() {
        return this.expression;
    }

    public void setExpression(Expression expression) {
        this.expression = expression;
    }
}
