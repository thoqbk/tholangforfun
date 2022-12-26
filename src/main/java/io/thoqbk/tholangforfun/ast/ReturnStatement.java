package io.thoqbk.tholangforfun.ast;

import io.thoqbk.tholangforfun.Token;
import io.thoqbk.tholangforfun.ast.expressions.Expression;

public class ReturnStatement extends Statement {
    private Expression value;

    public ReturnStatement(Token token) {
        super(token);
    }

    public Expression getValue() {
        return value;
    }

    public void setValue(Expression value) {
        this.value = value;
    }
}
