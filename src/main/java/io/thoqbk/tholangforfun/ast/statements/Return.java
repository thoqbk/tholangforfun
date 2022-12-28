package io.thoqbk.tholangforfun.ast.statements;

import io.thoqbk.tholangforfun.Token;
import io.thoqbk.tholangforfun.ast.expressions.Expression;

public class Return extends Statement {
    private Expression value;

    public Return(Token token) {
        super(token);
    }

    public Expression getValue() {
        return value;
    }

    public void setValue(Expression value) {
        this.value = value;
    }
}
