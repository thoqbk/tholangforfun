package io.thoqbk.tholangforfun.ast;

import io.thoqbk.tholangforfun.Token;
import io.thoqbk.tholangforfun.ast.expressions.Expression;

public class IfStatement extends Statement {
    private Expression condition;
    private BlockStatement ifBody;
    private BlockStatement elseBody;

    public IfStatement(Token token) {
        super(token);
    }

    public void setCondition(Expression expression) {
        condition = expression;
    }

    public Expression getCondition() {
        return condition;
    }

    public void setIfBody(BlockStatement statement) {
        ifBody = statement;
    }

    public BlockStatement getIfBody() {
        return ifBody;
    }

    public void setElseBody(BlockStatement statement) {
        elseBody = statement;
    }

    public BlockStatement getElseBody() {
        return elseBody;
    }
}
