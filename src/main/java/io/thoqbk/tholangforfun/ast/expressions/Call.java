package io.thoqbk.tholangforfun.ast.expressions;

import java.util.List;

import io.thoqbk.tholangforfun.Token;

public class Call extends Expression {
    private String functionName;
    private List<Expression> args;

    public Call(Token token) {
        super(token);
        this.functionName = token.getLiteral();
    }

    public String getFunctionName() {
        return functionName;
    }

    public void setArgs(List<Expression> args) {
        this.args = args;
    }

    public List<Expression> getArgs() {
        return args;
    }
}
