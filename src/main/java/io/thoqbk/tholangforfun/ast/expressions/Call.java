package io.thoqbk.tholangforfun.ast.expressions;

import java.util.List;

import io.thoqbk.tholangforfun.Token;

public class Call extends Expression {
    private Expression function; // Function or identifier
    private List<Expression> args;

    public Call(Token token, Expression function) {
        super(token);
        this.function = function;
    }

    public Expression getFunction() {
        return function;
    }

    public void setArgs(List<Expression> args) {
        this.args = args;
    }

    public List<Expression> getArgs() {
        return args;
    }
}
