package io.thoqbk.tholangforfun.ast.statements;

import io.thoqbk.tholangforfun.Token;
import io.thoqbk.tholangforfun.ast.expressions.Expression;

public class If extends Statement {
    private Expression condition;
    private Block ifBody;
    private Block elseBody;

    public If(Token token) {
        super(token);
    }

    public void setCondition(Expression expression) {
        condition = expression;
    }

    public Expression getCondition() {
        return condition;
    }

    public void setIfBody(Block statement) {
        ifBody = statement;
    }

    public Block getIfBody() {
        return ifBody;
    }

    public void setElseBody(Block statement) {
        elseBody = statement;
    }

    public Block getElseBody() {
        return elseBody;
    }
}
