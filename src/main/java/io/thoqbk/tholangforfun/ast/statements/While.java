package io.thoqbk.tholangforfun.ast.statements;

import io.thoqbk.tholangforfun.Token;
import io.thoqbk.tholangforfun.ast.expressions.Expression;

public class While extends Statement {
    private Expression condition;
    private Block body;

    public While(Token token) {
        super(token);
    }

    public void setCondition(Expression condition) {
        this.condition = condition;
    }

    public Expression getCondition() {
        return condition;
    }

    public void setBody(Block body) {
        this.body = body;
    }

    public Block getBody() {
        return body;
    }
}
