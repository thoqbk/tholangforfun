package io.thoqbk.tholangforfun.eval;

public class ReturnResult extends EvalResult {
    private EvalResult value;

    public ReturnResult(EvalResult value) {
        this.value = value;
    }

    public EvalResult getValue() {
        return value;
    }
}
