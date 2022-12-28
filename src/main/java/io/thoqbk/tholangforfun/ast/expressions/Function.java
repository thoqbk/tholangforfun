package io.thoqbk.tholangforfun.ast.expressions;

import java.util.List;

import io.thoqbk.tholangforfun.Token;
import io.thoqbk.tholangforfun.ast.statements.Block;

public class Function extends Expression {
    private List<Identifier> params;
    private Block body;

    public Function(Token token) {
        super(token);
    }

    public void setParams(List<Identifier> params) {
        this.params = params;
    }

    public List<Identifier> getParams() {
        return params;
    }

    public void setBody(Block body) {
        this.body = body;
    }

    public Block getBody() {
        return body;
    }
}
