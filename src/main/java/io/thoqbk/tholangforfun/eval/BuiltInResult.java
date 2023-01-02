package io.thoqbk.tholangforfun.eval;

public class BuiltInResult extends EvalResult {
    private final BuiltIn function;

    public BuiltInResult(BuiltIn function) {
        this.function = function;
    }

    public BuiltIn getFunction() {
        return function;
    }
}
