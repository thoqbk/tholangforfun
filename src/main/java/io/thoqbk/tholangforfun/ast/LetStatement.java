package io.thoqbk.tholangforfun.ast;

import io.thoqbk.tholangforfun.Token;

public class LetStatement extends Statement {
    private String variableName;
    private Expression expression;

    public LetStatement(Token token) {
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
