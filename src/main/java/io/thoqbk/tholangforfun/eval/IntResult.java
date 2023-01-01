package io.thoqbk.tholangforfun.eval;

public class IntResult extends EvalResult {
    private final int value;

    public IntResult(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "" + value;
    }
}
