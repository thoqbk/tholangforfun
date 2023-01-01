package io.thoqbk.tholangforfun.eval;

public class BoolResult extends EvalResult {
    private boolean value;

    public BoolResult(boolean value) {
        this.value = value;
    }

    public boolean getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "" + value;
    }
}
