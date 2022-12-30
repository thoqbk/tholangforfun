package io.thoqbk.tholangforfun.eval;

import java.util.List;

import io.thoqbk.tholangforfun.ast.statements.Block;

public class FunctionResult extends EvalResult {
    private final List<String> params;
    private final Block body;

    public FunctionResult(List<String> params, Block body) {
        this.params = params;
        this.body = body;
    }

    public List<String> getParams() {
        return params;
    }

    public Block getBody() {
        return body;
    }
}
